package com.mossy.boundedContext.app;

import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.global.jwt.JwtProperties;
import com.mossy.boundedContext.out.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public void save(Long userId, String refreshToken) {
        refreshTokenRepository.save(
                String.valueOf(userId),
                refreshToken,
                jwtProperties.refreshTokenExpireMs()
        );
    }

    @Transactional(readOnly = true)
    public String validateAndGetUserId(String refreshToken) {
        String userIdStr = refreshTokenRepository.getUserIdByToken(refreshToken);
        if (userIdStr == null) throw new DomainException(ErrorCode.INVALID_TOKEN);

        if (!refreshTokenRepository.existsInUserSet(userIdStr, refreshToken)) {
            throw new DomainException(ErrorCode.INVALID_TOKEN);
        }
        return userIdStr;
    }

    @Transactional
    public void delete(String userIdStr, String refreshToken) {
        refreshTokenRepository.delete(userIdStr, refreshToken);
    }

    @Transactional
    public void deleteIfExists(String refreshToken) {
        String userId = refreshTokenRepository.getUserIdByToken(refreshToken);
        if (userId != null) {
            refreshTokenRepository.delete(userId, refreshToken);
        }
    }
}
