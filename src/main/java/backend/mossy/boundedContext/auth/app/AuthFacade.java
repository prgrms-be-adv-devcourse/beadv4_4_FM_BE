package backend.mossy.boundedContext.auth.app;

import backend.mossy.boundedContext.auth.in.dto.LoginRequest;
import backend.mossy.boundedContext.auth.in.dto.LoginResponse;
import backend.mossy.boundedContext.auth.in.dto.TokenResponse;
import backend.mossy.boundedContext.auth.infra.jwt.JwtProperties;
import backend.mossy.boundedContext.auth.infra.jwt.JwtProvider;
import backend.mossy.boundedContext.auth.out.RefreshTokenRepository;
import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.boundedContext.member.out.UserRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.member.domain.role.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthFacade {
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    //로그인
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    @Transactional
    public LoginResponse login(LoginRequest request) {

        //이메일로 유저 찾기
        User user = userRepository.findByEmailWithRoles(request.email())
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        //비밀번호 대조
        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new DomainException(ErrorCode.INVALID_PASSWORD);
        }

        //권한 꺼내기
        String role = extractRole(user);

        //토큰 생성
        TokenResponse tokens = tokenService.issueTokens(user.getId(), role);

        refreshTokenRepository.save(
                String.valueOf(user.getId()),
                tokens.refreshToken(),
                jwtProperties.refreshTokenExpireMs()
        );

        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());
    }

    //토큰 재발급
    @Transactional
    public LoginResponse reissue(String refreshToken){

        String userIdStr = refreshTokenRepository.getUserIdByToken(refreshToken);

        if (userIdStr == null) {
            throw new DomainException(ErrorCode.INVALID_TOKEN);
        }

        if (!refreshTokenRepository.existsInUserSet(userIdStr, refreshToken)) {
            throw new DomainException(ErrorCode.INVALID_TOKEN);
        }

        //기존 토큰 삭제
        refreshTokenRepository.delete(userIdStr, refreshToken);

        //DB에서 다시 유저 정보 조회
        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        //권한 다시 추출
        String role = extractRole(user);

        // 새 토큰 발급
        TokenResponse tokens = tokenService.issueTokens(userId, role);

        refreshTokenRepository.save(
                userIdStr,
                tokens.refreshToken(),
                jwtProperties.refreshTokenExpireMs()
        );

        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());

    }


    //로그아웃
    @Transactional
    public  void logout(String refreshToken){
        String userId = refreshTokenRepository.getUserIdByToken(refreshToken);
        if (userId != null) {
            refreshTokenRepository.delete(userId, refreshToken);
        }
    }

    private String extractRole(User user) {
        List<UserRole> userRoles = user.getUserRoles();

        if (userRoles == null || userRoles.isEmpty()) {
            return "USER";
        }

        return userRoles.get(0).getRole().getCode().name();
    }
}
