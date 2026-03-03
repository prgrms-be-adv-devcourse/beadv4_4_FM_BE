package com.mossy.boundedContext.in;

import com.mossy.boundedContext.app.user.UserFacade;
import com.mossy.boundedContext.out.external.dto.request.MemberVerifyRequest;
import com.mossy.boundedContext.out.external.dto.response.MemberAuthInfoResponse;
import com.mossy.boundedContext.out.external.dto.response.MemberVerifyExternResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Internal-Member", description = "시스템 내부 서비스 간 회원 통신 API")
@RestController
@RequestMapping("/internal/v1/users")
@RequiredArgsConstructor
public class MemberInternalController {

    private final UserFacade userFacade;

    @Operation(summary = "회원 자격 검증", description = "이메일과 비밀번호를 받아 유효한 회원인지 확인합니다. (Auth 서비스 호출용)")
    @PostMapping("/verify")
    public MemberVerifyExternResponse verify(@RequestBody MemberVerifyRequest req) {
        return userFacade.verifyMember(req.email(), req.password());
    }

    @Operation(summary = "회원 인증 정보 조회 (내부전용)")
    @GetMapping("/id/{userId}")
    public MemberAuthInfoResponse getAuthInfo(@PathVariable Long userId) {
        return userFacade.getAuthInfo(userId);
    }
}
