package com.mossy.boundedContext.app;

import com.mossy.boundedContext.global.jwt.JwtProvider;
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
    private final JwtProvider jwtProvider;

    @Transactional
    public void save(Long userId, String refreshToken) {
        refreshTokenRepository.save(
                String.valueOf(userId),
                refreshToken,
                jwtProperties.refreshTokenExpireMs()
        );
    }

    @Transactional
    public Long rotate(String oldRefreshToken, String newRefreshToken) {
        //1. refreshtoken 서명/만료 검증 후 userId 추출
        final Long userId;
        try {
            userId = jwtProvider.getUserId(oldRefreshToken);
        } catch (Exception e) {
            throw new DomainException(ErrorCode.INVALID_TOKEN);
        }

        long result = refreshTokenRepository.rotate(
                String.valueOf(userId),
                oldRefreshToken,
                newRefreshToken,
                jwtProperties.refreshTokenExpireMs()
        );

        if (result == 1L) {
            return userId;
        }

        if (result == 0L) {
            throw new DomainException(ErrorCode.INVALID_TOKEN);
        }

        //result == -1 : 불일치
        throw new DomainException(ErrorCode.INVALID_TOKEN);
    }

    @Transactional
    public void delete(Long userId) {
        refreshTokenRepository.delete(String.valueOf(userId));
    }
}
