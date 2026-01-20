package backend.mossy.boundedContext.auth.app;

import backend.mossy.boundedContext.auth.in.dto.TokenResponse;
import backend.mossy.boundedContext.auth.infra.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;

    public TokenResponse issueTokens (Long userId, String role) {
        String accessToken = jwtProvider.createAccesToken(userId, role);
        String refreshToken = jwtProvider.createRefreshToken(userId);
        return new TokenResponse(accessToken, refreshToken);
    }
}
