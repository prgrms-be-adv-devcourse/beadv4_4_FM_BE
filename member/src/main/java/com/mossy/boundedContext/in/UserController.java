package com.mossy.boundedContext.in;

import com.mossy.boundedContext.app.user.UserFacade;
import com.mossy.boundedContext.in.dto.UserInfoDto;
import com.mossy.boundedContext.in.dto.request.ChangeAddressRequest;
import com.mossy.boundedContext.in.dto.request.ChangePasswordRequest;
import com.mossy.boundedContext.in.dto.request.ChangePhoneNumRequest;
import com.mossy.boundedContext.in.dto.request.ProfileUpdateRequest;
import com.mossy.boundedContext.in.dto.request.SetPasswordRequest;
import com.mossy.boundedContext.in.dto.request.SignupRequest;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "로그인 사용자 정보 조회 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;

    @Operation(summary = "회원가입", description = "일반 유저(USER)로 가입")
    @PostMapping("/signup")
    public RsData<Long> signup(@RequestBody SignupRequest req) {
        return RsData.success(SuccessCode.SIGNUP_COMPLETE, userFacade.signup(req));
    }

    @Operation(summary = "마이페이지", description = "현재 로그인된 사용자의 기본 식별 정보(userId)를 조회")
    @GetMapping("/me")
    public RsData<UserInfoDto> me(@RequestHeader("X-User-Id") Long userId) {
        UserInfoDto dto = userFacade.getUserInfo(userId);
        return RsData.success(SuccessCode.GET_MY_INFO_COMPLETE, dto);
    }

    @Operation(summary = "프로필 수정")
    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ProfileUpdateRequest request
    ) {
        userFacade.updateProfile(userId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "비밀번호 변경",
            description = "현재 비밀번호 확인 후 새 비밀번호로 변경. 소셜 전용 계정은 /set-password 먼저 사용"
    )
    @PatchMapping("/password")
    public RsData<Void> changePassword(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userFacade.changePassword(userId, request);
        return RsData.success(SuccessCode.CHANGE_PASSWORD_COMPLETE, null);
    }

    @Operation(
            summary = "주소 변경",
            description = "현재 비밀번호 확인 후 주소 변경. 소셜 전용 계정은 /set-password 먼저 사용"
    )
    @PatchMapping("/address")
    public RsData<Void> changeAddress(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ChangeAddressRequest request
    ) {
        userFacade.changeAddress(userId, request);
        return RsData.success(SuccessCode.CHANGE_ADDRESS_COMPLETE, null);
    }

    @Operation(
            summary = "전화번호 변경",
            description = "현재 비밀번호 확인 후 전화번호 변경. 소셜 전용 계정은 /set-password 먼저 사용"
    )
    @PatchMapping("/phone")
    public RsData<Void> changePhoneNum(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ChangePhoneNumRequest request
    ) {
        userFacade.changePhoneNum(userId, request);
        return RsData.success(SuccessCode.CHANGE_PHONE_COMPLETE, null);
    }

    @Operation(
            summary = "비밀번호 최초 설정 (소셜 전용 계정용)",
            description = "소셜 로그인만 있는 계정에서 최초로 비밀번호를 설정. 이미 비밀번호가 있으면 불가"
    )
    @PostMapping("/set-password")
    public RsData<Void> setPassword(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody SetPasswordRequest request
    ) {
        userFacade.setPassword(userId, request);
        return RsData.success(SuccessCode.SET_PASSWORD_COMPLETE, null);
    }
}

