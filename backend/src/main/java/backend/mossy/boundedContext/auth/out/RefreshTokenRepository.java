package backend.mossy.boundedContext.auth.out;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String KEY_TOKEN = "RT:TOKEN:";
    private static final String KEY_USER = "RT:USER:";

    public void save(String userId, String refreshToken, long ttlMillis) {

        String tokenKey = KEY_TOKEN + refreshToken;
        String userKey = KEY_USER + userId;

        redisTemplate.opsForValue().set(tokenKey, userId, Duration.ofMillis(ttlMillis));
        redisTemplate.opsForSet().add(userKey, refreshToken);
        redisTemplate.expire(userKey, Duration.ofMillis(ttlMillis));
    }

    public String getUserIdByToken(String refreshToken) {
        String result = redisTemplate.opsForValue().get(KEY_TOKEN + refreshToken);
        return result;
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
        String userKey = KEY_USER + userId;
        Set<String> tokens = redisTemplate.opsForSet().members(userKey);

        if (tokens != null && !tokens.isEmpty()) {
            List<String> keyToDelete = new ArrayList<>();
            for (String token : tokens) {
                keyToDelete.add(KEY_TOKEN + token);
            }
            redisTemplate.delete(keyToDelete);
        }
        redisTemplate.delete(userKey);
    }
}
