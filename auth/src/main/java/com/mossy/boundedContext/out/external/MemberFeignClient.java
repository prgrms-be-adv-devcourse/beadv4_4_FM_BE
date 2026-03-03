package com.mossy.boundedContext.out.external;

import com.mossy.boundedContext.out.dto.OAuth2UserDTO;
import com.mossy.boundedContext.out.dto.request.MemberVerifyExternRequest;
import com.mossy.boundedContext.out.dto.response.MemberAuthInfoResponse;
import com.mossy.boundedContext.out.dto.response.MemberVerifyResponse;
import com.mossy.boundedContext.out.dto.response.SocialLonginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "member", url = "${mossy.feign.member-url}")
public interface MemberFeignClient {

    @PostMapping("/internal/v1/users/verify")
    MemberVerifyResponse verify(@RequestBody MemberVerifyExternRequest request);

    @GetMapping("/internal/v1/users/id/{userId}")
    MemberAuthInfoResponse getAuthInfo(@PathVariable("userId") Long userId);

    @PostMapping("/internal/v1/users/social-login")
    SocialLonginResponse processSocialLogin(@RequestBody OAuth2UserDTO request);

    @DeleteMapping("/internal/v1/users/social-login/{userId}/rollback")
    void rollbackSocialLogin(@PathVariable("userId") Long userId);
}
