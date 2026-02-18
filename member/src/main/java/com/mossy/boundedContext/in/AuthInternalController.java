package com.mossy.boundedContext.in;

import com.mossy.boundedContext.in.dto.OAuth2UserDto;
import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.boundedContext.app.user.UserFacade;
import com.mossy.boundedContext.out.AuthFacade;
import com.mossy.boundedContext.out.external.dto.response.SocialLonginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Internal-Auth", description = "시스템 내부 서비스 간 인증/권한 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthInternalController {

    private final AuthFacade authFacade;
    private final UserFacade userFacade;

    @Operation(summary = "판매자 전용 토큰 발급", description = "판매자 승인 처리가 완료된 사용자에게 판매자 권한이 포함된 토큰을 발급합니다.")
    @PostMapping("/sellers/issue-seller-token")
    public LoginResponse issueForSellerApproved(
            @RequestParam Long userId, @RequestParam Long sellerId) {
        return authFacade.issueForSellerApproved(userId, sellerId);
    }

    @Operation(summary = "소셜 로그인 처리", description = "OAuth2 소셜 로그인 사용자 정보를 저장/업데이트합니다.")
    @PostMapping("/users/social-login")
    public SocialLonginResponse processSocialLogin(@RequestBody OAuth2UserDto userDTO) {
        return userFacade.processSocialLogin(userDTO);
    }
}
