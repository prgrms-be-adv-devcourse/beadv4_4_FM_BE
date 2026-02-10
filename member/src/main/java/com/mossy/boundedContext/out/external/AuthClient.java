package com.mossy.boundedContext.out.external;

import com.mossy.boundedContext.in.dto.response.LoginResponse;
import com.mossy.boundedContext.out.dto.request.MemberVerifyRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth", url = "localhost:8086")
public interface AuthClient {

    @PostMapping("/api/v1/member/auth/verify")
    void verify(@RequestBody MemberVerifyRequest request);

    @PostMapping("/api/v1/internal/auth/issue-seller-token")
    LoginResponse issueForSellerApproved(
            @RequestParam("userId") Long userId,
            @RequestParam("sellerId") Long sellerId
    );
}
