package com.mossy.boundedContext.in;

import com.mossy.boundedContext.app.user.UserFacade;
import com.mossy.shared.auth.domain.request.MemberVerifyRequest;
import com.mossy.shared.auth.domain.response.MemberVerifyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interbal/members")
@RequiredArgsConstructor
public class MemberInternalController {

    private final UserFacade userFacade;

    @PostMapping("/verify")
    public MemberVerifyResponse verify(@RequestBody MemberVerifyRequest req) {
        return userFacade.verifyMember(req.email(), req.password());
    }
}
