package backend.mossy.boundedContext.auth.app;

import backend.mossy.boundedContext.auth.in.dto.LoginRequest;
import backend.mossy.boundedContext.auth.in.dto.LoginResponse;
import backend.mossy.boundedContext.auth.in.dto.TokenResponse;
import backend.mossy.boundedContext.auth.out.RefreshTokenRepository;
import backend.mossy.boundedContext.member.domain.User;
import backend.mossy.boundedContext.member.out.user.UserRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.member.domain.role.RoleCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final TokenIssuer tokenIssuer;
    private final UserRepository userRepository;

    //로그인
    @Transactional
    public LoginResponse login(LoginRequest request) {

        var ctx = loginUseCase.execute(request.email(), request.password());
        TokenResponse tokens = tokenIssuer.issueTokens(ctx.userId(),ctx.role());
        refreshTokenUseCase.save(ctx.userId(),tokens.refreshToken());
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());

    }

    //토큰 재발급
    @Transactional
    public LoginResponse reissue(String refreshToken){

        String userIdStr = refreshTokenUseCase.validateAndGetUserId(refreshToken);

        refreshTokenUseCase.delete(userIdStr, refreshToken);

        Long userId = Long.valueOf(userIdStr);
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        String role = user.getPrimaryRole().name();

        TokenResponse tokens = tokenIssuer.issueTokens(userId,role);

        refreshTokenUseCase.save(userId,tokens.refreshToken());
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());
    }

    //로그아웃
    @Transactional
    public  void logout(String refreshToken){
        refreshTokenUseCase.deleteIfExists(refreshToken);
    }
}
