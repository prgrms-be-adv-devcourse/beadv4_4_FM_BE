package src.main.java.com.mossy.auth.app;

import com.mossy.member.auth.infra.jwt.JwtProperties;
import com.mossy.member.auth.out.RefreshTokenRepository;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public void save(Long userId, String refreshToken) {
        refreshTokenRepository.save(
                String.valueOf(userId),
                refreshToken,
                jwtProperties.refreshTokenExpireMs()
        );
    }

    public String validateAndGetUserId(String refreshToken) {
        String userIdStr = refreshTokenRepository.getUserIdByToken(refreshToken);
        if (userIdStr == null) throw new DomainException(ErrorCode.INVALID_TOKEN);

        if (!refreshTokenRepository.existsInUserSet(userIdStr, refreshToken)) {
            throw new DomainException(ErrorCode.INVALID_TOKEN);
        }
        return userIdStr;
    }

    public void delete(String userIdStr, String refreshToken) {
        refreshTokenRepository.delete(userIdStr, refreshToken);
    }

    public void deleteIfExists(String refreshToken) {
        String userId = refreshTokenRepository.getUserIdByToken(refreshToken);
        if (userId != null) {
            refreshTokenRepository.delete(userId, refreshToken);
        }
    }
}
