package com.mossy.boundedContext.out.external;

import com.mossy.boundedContext.out.external.dto.response.TokenIssueResponse;
import com.mossy.global.rsData.RsData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth", url = "${mossy.feign.auth-url}")
public interface AuthFeignClient {


    @PostMapping("/api/v1/auth/seller-approved")
    RsData<TokenIssueResponse> issueForSellerApproved(
            @RequestParam("userId") Long userId,
            @RequestParam("sellerId") Long sellerId
    );
}
