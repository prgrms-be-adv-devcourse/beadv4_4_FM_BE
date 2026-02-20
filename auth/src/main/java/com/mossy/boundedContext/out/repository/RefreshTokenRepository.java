package com.mossy.boundedContext.out.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;


@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private static final String KEY_PREFIX = "RT:USER:"; // RT:USER:{userId}
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    // Rotate 결과 상수
    public static final long ROTATE_SUCCESS = 1L;
    public static final long ROTATE_SESSION_NOT_FOUND = 0L;
    public static final long ROTATE_TOKEN_MISMATCH = -1L;

    private final StringRedisTemplate redisTemplate;

    @Value("${security.refresh-token.pepper}")
    private String pepper;

    /**
     * Lua 스크립트를 통한 원자적 토큰 교체
     * 1: 성공
     * 0: 세션 없음 (만료/로그아웃)
     * -1: 토큰 불일치 (재사용 의심)
     */
    private static final DefaultRedisScript<Long> ROTATE_SCRIPT = new DefaultRedisScript<>(
            """
            local key = KEYS[1]
            local old = ARGV[1]
            local newv = ARGV[2]
            local ttl = tonumber(ARGV[3])
            
            local cur = redis.call('GET', key)
            
            -- 저장된 토큰이 없음 (만료 또는 로그아웃)
            if not cur then
                return 0
            end
            
            -- 토큰 불일치 (이미 사용되었거나 탈취됨)
            if cur ~= old then 
                return -1
            end
            
            -- 토큰 교체
            redis.call('SET', key, newv, 'PX', ttl)
            return 1
            """,
            Long.class
    );

    //최초 로그인 시 RefreshToken 저장
    public void save(String userId, String refreshToken, long ttlMills) {
        String key = KEY_PREFIX + userId;
        String hashed = hash(refreshToken);
        redisTemplate.opsForValue().set(key, hashed, Duration.ofMillis(ttlMills));
    }

    //
    public long rotate(String userId, String oldToken, String newToken, long ttlMills) {
        String key = KEY_PREFIX + userId;
        String oldHashed = hash(oldToken);
        String newHashed = hash(newToken);

        Long result = redisTemplate.execute(
                ROTATE_SCRIPT,
                Collections.singletonList(key),
                oldHashed, newHashed, String.valueOf(ttlMills)
        );

        return result == null ? ROTATE_SESSION_NOT_FOUND : result;
    }

    //로그아웃 시 RefreshToken 삭제
    public void delete(String userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }

    //HMAC-SHA256으로 토큰 해싱
    private String hash(String token) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(pepper.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] digest = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("RefreshToken 해싱 실패", e);
        }
    }

    //바이트 배열을 16진수 문자열로 변환
    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}