package com.mossy.boundedContext.app.user;

import com.mossy.boundedContext.app.VerfyMemberUseCase;
import com.mossy.boundedContext.domain.user.User;
import com.mossy.boundedContext.in.dto.UserInfoDto;
import com.mossy.boundedContext.in.dto.request.ChangeAddressRequest;
import com.mossy.boundedContext.in.dto.request.ChangeNicknameRequest;
import com.mossy.boundedContext.in.dto.request.ChangePasswordRequest;
import com.mossy.boundedContext.in.dto.request.ChangePhoneNumRequest;
import com.mossy.boundedContext.in.dto.request.ProfileUpdateRequest;
import com.mossy.boundedContext.in.dto.request.SetPasswordRequest;
import com.mossy.boundedContext.in.dto.request.SignupRequest;
import com.mossy.boundedContext.in.dto.OAuth2UserDto;
import com.mossy.boundedContext.out.external.dto.response.MemberAuthInfoResponse;
import com.mossy.boundedContext.out.external.dto.response.SocialLonginResponse;
import com.mossy.boundedContext.out.external.dto.response.MemberVerifyExternResponse;
import com.mossy.kafka.publisher.KafkaEventPublisher;
import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.boundedContext.app.mapper.UserMapper;
import com.mossy.shared.member.payload.UserPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacade {

    private final SignupUseCase signupUseCase;
    private final ProcessSocialLoginUseCase processSocialLoginUseCase;
    private final GetUserInfoUseCase getUserInfoUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ChangeAddressUseCase changeAddressUseCase;
    private final ChangePhoneNumUseCase changePhoneNumUseCase;
    private final ChangeNicknameUseCase changeNicknameUseCase;
    private final ChangeProfileImageUseCase changeProfileImageUseCase;
    private final SetPasswordUseCase setPasswordUseCase;
    private final VerfyMemberUseCase verfyMemberUseCase;
    private final UserMapper mapper;
    private final KafkaEventPublisher kafkaEventPublisher;

    //회원가입
    @Transactional
    public Long signup(SignupRequest req, MultipartFile profileImage) {
        User savedUser = signupUseCase.execute(req, profileImage);
        UserPayload userPayload = mapper.toPayload(savedUser);
        kafkaEventPublisher.publish(new UserJoinedEvent(userPayload));
        return savedUser.getId();
    }

    // 프로필 이미지 변경
    public String changeProfileImage(Long userId, MultipartFile file) {
        return changeProfileImageUseCase.execute(userId, file);
    }

    // 프로필 이미지 삭제 (기본 이미지로 복원)
    public void deleteProfileImage(Long userId) {
        changeProfileImageUseCase.deleteAndResetToDefault(userId);
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

    // 비밀번호 변경 (현재 비밀번호 확인 후 변경)
    public void changePassword(Long userId, ChangePasswordRequest request) {
        changePasswordUseCase.execute(userId, request);
    }

    // 주소 변경 (현재 비밀번호 확인 후 변경)
    public void changeAddress(Long userId, ChangeAddressRequest request) {
        changeAddressUseCase.execute(userId, request);
    }

    // 전화번호 변경 (현재 비밀번호 확인 후 변경)
    public void changePhoneNum(Long userId, ChangePhoneNumRequest request) {
        changePhoneNumUseCase.execute(userId, request);
    }

    // 닉네임 변경
    public void changeNickname(Long userId, ChangeNicknameRequest request) {
        changeNicknameUseCase.execute(userId, request);
    }

    // 소셜 로그인 전용 계정 → 최초 비밀번호 설정
    public void setPassword(Long userId, SetPasswordRequest request) {
        setPasswordUseCase.execute(userId, request);
    }
}