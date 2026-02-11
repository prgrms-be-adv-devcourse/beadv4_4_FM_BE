package com.mossy.boundedContext.in;

import com.mossy.boundedContext.app.user.UserFacade;
import com.mossy.boundedContext.out.external.dto.request.MemberVerifyRequest;
import com.mossy.boundedContext.out.external.dto.response.MemberVerifyExternResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Internal-Member", description = "시스템 내부 서비스 간 회원 통신 API")
@RestController
@RequestMapping("api/v1/auth/users")
@RequiredArgsConstructor
public class MemberInternalController {

    private final UserFacade userFacade;

    @Operation(summary = "회원 자격 검증", description = "이메일과 비밀번호를 받아 유효한 회원인지 확인합니다. (Auth 서비스 호출용)")
    @PostMapping("/verify")
    public MemberVerifyExternResponse verify(@RequestBody MemberVerifyRequest req) {
        return userFacade.verifyMember(req.email(), req.password());
    }
}
