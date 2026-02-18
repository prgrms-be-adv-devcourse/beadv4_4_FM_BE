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

    private final StringRedisTemplate redisTemplate;

    private final String KEY_USER = "RT:USER:"; // RT:USER:{userId}

    //rotate 결과
    // 1: 성공
    // 0: 세션 없음(만료/로그아웃)
    // -1: 불일치(재시도/레이스/재사용 의심)

    private static final DefaultRedisScript<Long> ROTATE_SCRIPT = new DefaultRedisScript<>(
            """
            local key = KEYS[1]
            local old = ARGV[1]
            local newv  = ARGV[2]
            local ttl = tonumber(ARGV[3])
            
            local cur = redis.call('GET', key)
            
            --1. 저장된 토큰이 아예 없음(만료 혹은 로그아웃)
            if not cur then
                return 0
            end
            
            --2. 토큰 불일치(이미 사용되었거나 탈취됨)
            if cur ~= old then 
                return -1
            end
            
            redis.call('SET', key, newv, 'PX', ttl)
            return 1
            
            """,
            Long.class
    );

    @Value("${security.refresh-token.pepper}")
    private String pepper;

    //최초 로그인시
    public void save(String userId, String refreshToken, long ttlMills) {
        String key = KEY_USER + userId;
        String hashed = hash(refreshToken);
        redisTemplate.opsForValue().set(key, hashed, Duration.ofMillis(ttlMills));
    }

    public long rotate(String userId, String oldToken, String newToken, long ttlMills) {
        String key = KEY_USER + userId;

        String oldHashed = hash(oldToken);
        String newHashed = hash(newToken);

        Long result = redisTemplate.execute(
                ROTATE_SCRIPT,
                Collections.singletonList(key),
                oldHashed, newHashed, String.valueOf(ttlMills)
        );

        return result == null ? 0L : result;
    }

    //로그아웃
    public void delete(String userId) {
        redisTemplate.delete(KEY_USER + userId);
    }

    //HMAC-SHA256(token, pepper)
    private String hash(String token) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(pepper.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash refresh token", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for(byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}