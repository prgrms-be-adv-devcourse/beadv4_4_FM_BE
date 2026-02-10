package com.mossy.boundedContext.in;

import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.boundedContext.out.AuthFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Internal-Auth", description = "시스템 내부 서비스 간 인증/권한 API")
@RestController
@RequestMapping("/api/v1/auth/internal")
@RequiredArgsConstructor
public class AuthInternalController {

    private final AuthFacade authFacade;

    @Operation(summary = "판매자 전용 토큰 발급", description = "판매자 승인 처리가 완료된 사용자에게 판매자 권한이 포함된 토큰을 발급합니다.")
    @PostMapping("/issue-seller-token")
    public LoginResponse issueForSellerApproved(
            @RequestParam Long userId, @RequestParam Long sellerId) {
        return authFacade.issueForSellerApproved(userId, sellerId);
    }
}
