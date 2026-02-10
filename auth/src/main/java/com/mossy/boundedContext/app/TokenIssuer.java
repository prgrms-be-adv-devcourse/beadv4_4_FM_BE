package com.mossy.boundedContext.app;

import com.mossy.boundedContext.in.dto.TokenResponse;
import com.mossy.boundedContext.infra.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenIssuer {

    private final JwtProvider jwtProvider;

    public TokenResponse issueTokens (Long userId, String role, Long sellerId) {
        String accessToken = jwtProvider.createAccessToken(userId, role, sellerId);
        String refreshToken = jwtProvider.createRefreshToken(userId);
        return new TokenResponse(accessToken, refreshToken);
    }
}
