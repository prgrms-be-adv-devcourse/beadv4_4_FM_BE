package backend.mossy.boundedContext.auth.out;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_TOKEN = "RT:TOKEN:";
    private static final String KEY_USER = "RT:USER:";

    public void save(String userId, String refreshToken, long ttlMillis) {

        redisTemplate.opsForValue().set(KEY_TOKEN + refreshToken, userId, Duration.ofMillis(ttlMillis));
        redisTemplate.opsForSet().add(KEY_USER + userId, refreshToken);
        redisTemplate.expire(KEY_USER + userId, Duration.ofMillis(ttlMillis));
    }

    public String getUserIdByToken(String refreshToken) {
        Object result = redisTemplate.opsForValue().get(KEY_TOKEN + refreshToken);
        return result != null ? result.toString() : null;
    }

    public boolean existsInUserSet(String userId, String refreshToken) {
        Boolean isMember = redisTemplate.opsForSet().isMember(KEY_USER + userId, refreshToken);
        return Boolean.TRUE.equals(isMember);
    }

    //로그인시 토큰 삭제
    public void delete(String userId, String refreshToken) {
        redisTemplate.delete(KEY_TOKEN + refreshToken);
        redisTemplate.opsForSet().remove(KEY_USER + userId, refreshToken);
    }

    public void deleteAllByUser(String userId) {
        var tokens = redisTemplate.opsForSet().members(KEY_USER + userId);

        if (tokens != null && !tokens.isEmpty()) {
            for (Object token : tokens) {
                redisTemplate.delete(KEY_TOKEN + token.toString());
            }
        }

        redisTemplate.delete(KEY_USER + userId);
    }
}
