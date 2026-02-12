package com.mossy.boundedContext.out.external;

import com.mossy.boundedContext.out.dto.request.MemberVerifyExternRequest;
import com.mossy.boundedContext.out.dto.response.MemberAuthInfoResponse;
import com.mossy.boundedContext.out.dto.response.MemberVerifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "member", url = "http://localhost:8082")
public interface MemberFeignClient {

    @PostMapping("api/v1/auth/users/verify")
    MemberVerifyResponse verify(@RequestBody MemberVerifyExternRequest request);

    @GetMapping("/internal/auth/{userId")
    MemberAuthInfoResponse getAuthInfo(@PathVariable("userId") Long userId);
}
