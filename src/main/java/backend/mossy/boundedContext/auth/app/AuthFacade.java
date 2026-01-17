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

    //토큰 재발급
    @Transactional
    public LoginResponse reissue(String refreshToken){

        String userId = refreshTokenRepository.getUserIdByToken(refreshToken);

        if (userId == null) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 토큰입니다."); //변경할 예정
        }

        if (!refreshTokenRepository.existsInUserSet(userId, refreshToken)) {
            throw new IllegalArgumentException("사용할 수 없는 토큰입니다.");
        }

        refreshTokenRepository.delete(userId, refreshToken);

        String newAccessToken = jwtProvider.createAccesToken(Long.valueOf(userId), "USER"); //Role은 나중에 DB에서 가져오기
        String newRefreshToken = jwtProvider.createRefreshToken(Long.valueOf(userId));

        refreshTokenRepository.save(
                userId,
                newRefreshToken,
                jwtProperties.refreshTokenExpireMs()
        );

        return new LoginResponse(newAccessToken, newRefreshToken);

    }


    //로그아웃
    @Transactional
    public  void logout(String refreshToken){
        String userId = refreshTokenRepository.getUserIdByToken(refreshToken);
        if (userId == null) {
            refreshTokenRepository.delete(userId, refreshToken);
        }
    }

}
