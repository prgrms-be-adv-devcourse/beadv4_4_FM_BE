package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.app.VerfyMemberUseCase;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.in.dto.UserInfoDto;
import com.mossy.boundedContext.in.dto.request.ProfileUpdateRequest;
import com.mossy.boundedContext.in.dto.request.SignupRequest;
import com.mossy.boundedContext.in.dto.OAuth2UserDto;
import com.mossy.boundedContext.out.external.dto.response.MemberAuthInfoResponse;
import com.mossy.boundedContext.out.external.dto.response.SocialLonginResponse;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.boundedContext.out.external.dto.response.MemberVerifyExternResponse;
import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.boundedContext.app.mapper.UserMapper;
import com.mossy.shared.member.payload.UserPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacade {

    private final SignupUseCase signupUseCase;
    private final ProcessSocialLoginUseCase processSocialLoginUseCase;
    private final GetUserInfoUseCase getUserInfoUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final VerfyMemberUseCase verfyMemberUseCase;
    private final UserMapper mapper;
    private final EventPublisher eventPublisher;

    //회원가입
    public Long signup(SignupRequest req) {
        User savedUser = signupUseCase.execute(req);
        UserPayload userPayload = mapper.toPayload(savedUser);
        eventPublisher.publish(new UserJoinedEvent(userPayload));
        return savedUser.getId();
    }

    //사용자 정보 조회 (판매자 신청 상태 포함)
    public UserInfoDto getUserInfo(Long userId) {
        return getUserInfoUseCase.infoExecute(userId);
    }

    //회원 인증 (Auth 서비스용)
    public MemberVerifyExternResponse verifyMember(String email, String password) {
        return verfyMemberUseCase.execute(email, password);
    }

    //인증 정보 조회 (Auth 서비스용)
    public MemberAuthInfoResponse getAuthInfo(Long userId) {
        return getUserInfoUseCase.tokenExecute(userId);
    }

    //OAuth2 소셜 로그인 처리
    public SocialLonginResponse processSocialLogin(OAuth2UserDto userDTO) {
        return processSocialLoginUseCase.execute(userDTO);
    }

    // 소셜 로그인 후 토큰 발급 실패 시 보상 트랜잭션 - 생성된 유저 롤백
    public void rollbackSocialLogin(Long userId) {
        processSocialLoginUseCase.rollback(userId);
    }

    //프로필 수정
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        updateProfileUseCase.execute(userId, request);
    }
}