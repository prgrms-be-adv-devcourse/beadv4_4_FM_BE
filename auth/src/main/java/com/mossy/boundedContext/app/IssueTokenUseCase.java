package com.mossy.boundedContext.app;

import com.mossy.boundedContext.in.dto.response.TokenResponse;
import com.mossy.boundedContext.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IssueTokenUseCase {

    private final JwtProvider jwtProvider;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @Transactional
    public TokenResponse execute (Long userId, String role, Long sellerId) {
        String accessToken = jwtProvider.createAccessToken(userId, role, sellerId);
        String refreshToken = jwtProvider.createRefreshToken(userId);
        refreshTokenUseCase.save(userId, refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }
}
