package com.mossy.boundedContext.in;


import com.mossy.boundedContext.app.user.UserFacade;
import com.mossy.boundedContext.in.dto.UserInfoDto;
import com.mossy.boundedContext.in.dto.request.ProfileUpdateRequest;
import com.mossy.boundedContext.in.dto.request.SignupRequest;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "로그인 사용자 정보 조회 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserFacade userFacade;

    @Operation(
            summary = "회원가입",
            description = "일반 유저(USER)로 가입")
    @PostMapping("/signup")
    public RsData<Long> signup(@RequestBody SignupRequest req) {
        return RsData.success(SuccessCode.SIGNUP_COMPLETE, userFacade.signup(req));
    }

    @Operation(
            summary = "마이페이지",
            description = "현재 로그인된 사용자의 기본 식별 정보(userId)를 조회"
    )
    @GetMapping("/me")
    public RsData<UserInfoDto> me(@RequestHeader("X-User-Id") Long userId) {
        UserInfoDto dto = userFacade.getUserInfo(userId);
        return RsData.success(SuccessCode.GET_MY_INFO_COMPLETE, dto);
    }

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody ProfileUpdateRequest request
    ) {
        userFacade.updateProfile(userId, request);
        return ResponseEntity.ok().build();
    }
}
