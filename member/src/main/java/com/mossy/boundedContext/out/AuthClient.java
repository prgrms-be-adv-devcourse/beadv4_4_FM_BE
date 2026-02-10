package com.mossy.boundedContext.out;

import com.mossy.boundedContext.in.dto.response.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth", url = "localhost:8086")
public interface AuthClient {

    @PostMapping("/api/v1/auth/internal/issue-seller-token")
    LoginResponse issueForSellerApproved(
            @RequestParam("userId") Long userId,
            @RequestParam("sellerId") Long sellerId
    );
}
