package com.mossy.boundedContext.app;

import com.mossy.boundedContext.in.dto.request.LoginRequest;
import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.boundedContext.in.dto.response.TokenResponse;
import com.mossy.boundedContext.out.dto.OAuth2UserDTO;
import com.mossy.boundedContext.out.dto.response.SocialLonginResponse;
import com.mossy.boundedContext.out.external.MemberFeignClient;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 인증 관련 비즈니스 로직을 조율하는 Facade 클래스
 * 로그인, 토큰 재발급, 로그아웃, OAuth2 소셜 로그인을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final IssueTokenUseCase issueTokenUseCase;
    private final ReissueTokenUseCase reissueTokenUseCase;
    private final MemberFeignClient memberFeignClient;

    /**
     * 일반 로그인 (이메일/비밀번호)
     */
    public LoginResponse login(LoginRequest request) {
        var ctx = loginUseCase.execute(request.email(), request.password());
        TokenResponse tokens = issueTokenUseCase.execute(ctx.userId(), ctx.role(), null);
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken(), false);
    }

    /**
     * 토큰 재발급
     */
    public LoginResponse reissue(String oldRefreshToken) {
        TokenResponse tokens = reissueTokenUseCase.execute(oldRefreshToken);
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken(), false);
    }

    /**
     * 로그아웃
     */
    public void logout(String refreshToken) {
        logoutUseCase.execute(refreshToken);
    }

    /**
     * 판매자 승인 후 토큰 재발급
     */
    public LoginResponse issueForSellerApproved(Long userId, Long sellerId) {
        TokenResponse tokens = issueTokenUseCase.execute(userId, "SELLER", sellerId);
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken(), false);
    }

    /**
     * OAuth2 로그인 처리 및 토큰 발급
     */
    public LoginResponse upsertUserAndIssueToken(OAuth2UserDTO userDTO) {
        log.info("OAuth2 사용자 처리 시작: provider={}, email={}", userDTO.provider(), userDTO.email());

        try {
            SocialLonginResponse user = memberFeignClient.processSocialLogin(userDTO);
            TokenResponse tokens = issueTokenUseCase.execute(user.id(), "USER", null);
            return new LoginResponse(tokens.accessToken(), tokens.refreshToken(), user.isNewUser());
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new DomainException(ErrorCode.MEMBER_SERVICE_UNAVAILABLE);
        }
    }

}
