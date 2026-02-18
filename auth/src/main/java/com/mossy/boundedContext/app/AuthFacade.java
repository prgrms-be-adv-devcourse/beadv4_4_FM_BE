package com.mossy.boundedContext.app;

import  com.mossy.boundedContext.in.dto.request.LoginRequest;
import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.boundedContext.in.dto.response.TokenResponse;
import com.mossy.boundedContext.out.dto.OAuth2UserInfo;
import com.mossy.boundedContext.out.dto.OAuth2UserDTO;
import com.mossy.boundedContext.out.external.MemberFeignClient;
import com.mossy.shared.member.domain.entity.BaseUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final IssueTokenUseCase issueTokenUseCase;
    private final ReissueTokenUseCase reissueTokenUseCase;
    private final MemberFeignClient memberFeignClient;

    //로그인
    public LoginResponse login(LoginRequest request) {

        var ctx = loginUseCase.execute(request.email(), request.password());

        //TODO: null자리에 sellerId 넣기(아직 sellerClient 안만들었음)
        TokenResponse tokens = issueTokenUseCase.execute(ctx.userId(),ctx.role(), null);
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());

    }

    //토큰 재발급
    public LoginResponse reissue(String oldRefreshToken) {
        TokenResponse tokens = reissueTokenUseCase.execute(oldRefreshToken);
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());
    }

    //로그아웃
    public void logout(String refreshToken){
        logoutUseCase.execute(refreshToken);
    }

    //판매자 등록
    public LoginResponse issueForSellerApproved(Long userId, Long sellerId) {
        TokenResponse tokens = issueTokenUseCase.execute(userId, "SELLER", sellerId);
        return new LoginResponse(tokens.accessToken(), tokens.refreshToken());
    }

    //소셜로그인
    @SuppressWarnings("unused")
    public BaseUser upsertUser(OAuth2UserInfo userInfo) {
        //1. 유젓 서비스에 이멜로 기존 유저 확인
        //2. 있으면 해당 유저 정보 반환
        //3. 없으면 소셜 정보를 바탄으로 신규 회원가입 처리

        OAuth2UserDTO userDTO = new OAuth2UserDTO(
                userInfo.providerId(),
                userInfo.provider(),
                userInfo.email(),
                userInfo.name(),
                null
        );

        return memberFeignClient.processSocialLogin(userDTO);
    }

    //OAuth2 로그인 처리 및 토큰 발급
    public LoginResponse upsertUserAndIssueToken(OAuth2UserDTO userDTO) {
        log.info("OAuth2 사용자 처리 시작: provider={}, email={}", userDTO.provider(), userDTO.email());

        try {
            // Member 서비스에서 사용자 정보 저장/업데이트
            BaseUser user = memberFeignClient.processSocialLogin(userDTO);

            log.info("사용자 정보 저장/업데이트 완료: userId={}", user.getId());

            // TODO: null자리에 sellerId 넣기(아직 sellerClient 안만들었음)
            TokenResponse tokens = issueTokenUseCase.execute(user.getId(), "USER", null);

            log.info("토큰 발급 완료: userId={}", user.getId());

            return new LoginResponse(tokens.accessToken(), tokens.refreshToken());
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생", e);
            throw new RuntimeException("OAuth2 로그인 처리 실패", e);
        }
    }

}
