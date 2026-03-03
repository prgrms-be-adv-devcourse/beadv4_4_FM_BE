package com.mossy.boundedContext.app;

import com.mossy.boundedContext.in.dto.response.TokenResponse;
import com.mossy.boundedContext.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueTokenUseCase {

    private final JwtProvider jwtProvider;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @Transactional
    public TokenResponse execute (Long userId, List<String> roles, Long sellerId) {
        String accessToken = jwtProvider.createAccessToken(userId, roles, sellerId);
        String refreshToken = jwtProvider.createRefreshToken(userId);
        refreshTokenUseCase.save(userId, refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }
}
