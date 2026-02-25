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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final IssueTokenUseCase issueTokenUseCase;
    private final ReissueTokenUseCase reissueTokenUseCase;
    private final MemberFeignClient memberFeignClient;

    //일반 로그인 (이메일/비밀번호)
    public LoginResponse login(LoginRequest request) {
        var ctx = loginUseCase.execute(request.email(), request.password());
        log.info("[auth] issueTokenUseCase: userId={}, roles={}, sellerId={}", ctx.userId(), ctx.roles(), ctx.sellerId());
        TokenResponse tokens = issueTokenUseCase.execute(ctx.userId(), ctx.roles(), ctx.sellerId());
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken(), false, getPrimaryRole(ctx.roles()));
    }

    //토큰 재발급
    public LoginResponse reissue(String oldRefreshToken) {
        ReissueTokenUseCase.ReissueResult result = reissueTokenUseCase.execute(oldRefreshToken);
        return new LoginResponse(result.accessToken(), result.refreshToken(), false, result.primaryRole());
    }

    //로그아웃
    public void logout(String refreshToken) {
        logoutUseCase.execute(refreshToken);
    }

    //판매자 승인 후 토큰 재발급
    public LoginResponse issueForSellerApproved(Long userId, Long sellerId) {
        TokenResponse tokens = issueTokenUseCase.execute(userId, List.of("USER", "SELLER"), sellerId);
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken(), false, "SELLER");
    }

    private String getPrimaryRole(List<String> roles) {
        if (roles.contains("ADMIN")) return "ADMIN";
        if (roles.contains("SELLER")) return "SELLER";
        return "USER";
    }

    //OAuth2 로그인 처리 및 토큰 발급
    public LoginResponse upsertUserAndIssueToken(OAuth2UserDTO userDTO) {
        log.info("OAuth2 사용자 처리 시작: provider={}, email={}", userDTO.provider(), userDTO.email());

        // 1단계: member 서비스에 유저 저장
        SocialLonginResponse user;
        try {
            user = memberFeignClient.processSocialLogin(userDTO);
        } catch (Exception e) {
            log.error("소셜 유저 저장 실패: {}", e.getMessage(), e);
            throw new DomainException(ErrorCode.MEMBER_SERVICE_UNAVAILABLE);
        }

        // 2단계: 토큰 발급 - 실패 시 member 보상 트랜잭션 호출
        try {
            TokenResponse tokens = issueTokenUseCase.execute(user.id(), List.of("USER"), null);
            return new LoginResponse(tokens.accessToken(), tokens.refreshToken(), user.isNewUser(), "USER");
        } catch (Exception e) {
            log.error("토큰 발급 실패, member 보상 트랜잭션 실행: userId={}, error={}", user.id(), e.getMessage(), e);
            try {
                memberFeignClient.rollbackSocialLogin(user.id());
                log.info("member 보상 트랜잭션 완료: userId={}", user.id());
            } catch (Exception rollbackEx) {
                // 롤백 자체가 실패해도 로그만 남기고 원래 에러를 던짐
                log.error("member 보상 트랜잭션 실패 (수동 정리 필요): userId={}, error={}", user.id(), rollbackEx.getMessage());
            }
            throw new DomainException(ErrorCode.MEMBER_SERVICE_UNAVAILABLE);
        }
    }

}
