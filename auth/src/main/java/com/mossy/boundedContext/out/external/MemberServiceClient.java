package com.mossy.boundedContext.out.external;

import com.mossy.boundedContext.out.dto.request.MemberVerifyExternRequest;
import com.mossy.boundedContext.out.dto.response.MemberVerifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "member", url = "http://localhost:8082")
public interface MemberServiceClient {

    @PostMapping("api/v1/internal/members/verify")
    MemberVerifyResponse verify(@RequestBody MemberVerifyExternRequest request);
}
