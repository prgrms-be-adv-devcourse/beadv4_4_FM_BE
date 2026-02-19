package com.mossy.boundedContext.out.external;

import com.mossy.boundedContext.out.dto.OAuth2UserDTO;
import com.mossy.boundedContext.out.dto.request.MemberVerifyExternRequest;
import com.mossy.boundedContext.out.dto.response.MemberAuthInfoResponse;
import com.mossy.boundedContext.out.dto.response.MemberVerifyResponse;
import com.mossy.boundedContext.out.dto.response.SocialLonginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "member", url = "http://localhost:8082")
public interface MemberFeignClient {

    @PostMapping("api/v1/auth/users/verify")
    MemberVerifyResponse verify(@RequestBody MemberVerifyExternRequest request);

    @GetMapping("api/v1/auth/users/{userId}")
    MemberAuthInfoResponse getAuthInfo(@PathVariable("userId") Long userId);

    @PostMapping("/api/v1/auth/users/social-login")
    SocialLonginResponse processSocialLogin(@RequestBody OAuth2UserDTO request);

}
