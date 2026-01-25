package backend.mossy.boundedContext.auth.app;

import backend.mossy.boundedContext.auth.in.dto.TokenResponse;
import backend.mossy.boundedContext.auth.infra.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenIssuer {

    private final JwtProvider jwtProvider;

    public TokenResponse issueTokens (Long userId, String role) {
        return issueTokens(userId, role, null);
    }
    public TokenResponse issueTokens (Long userId, String role, Long sellerId) {
        String accessToken = jwtProvider.createAccessToken(userId, role, sellerId);
        String refreshToken = jwtProvider.createRefreshToken(userId);
        return new TokenResponse(accessToken, refreshToken);
    }
}
