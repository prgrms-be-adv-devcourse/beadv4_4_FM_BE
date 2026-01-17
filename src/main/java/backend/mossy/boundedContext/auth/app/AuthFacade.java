package backend.mossy.boundedContext.auth.app;

import backend.mossy.boundedContext.auth.in.dto.LoginResponse;
import backend.mossy.boundedContext.auth.infra.jwt.JwtProperties;
import backend.mossy.boundedContext.auth.infra.jwt.JwtProvider;
import backend.mossy.boundedContext.auth.out.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacade {
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public LoginResponse login(Long userId, String role) {
        //TODO: 유저 검증 로직 불러오기

        String accessToken = jwtProvider.createAccesToken(userId, role);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        refreshTokenRepository.save(
                String.valueOf(userId),
                refreshToken,
                jwtProperties.refreshTokenExpireMs()
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse reissue(String refreshToken){

        return null;
    }
}
