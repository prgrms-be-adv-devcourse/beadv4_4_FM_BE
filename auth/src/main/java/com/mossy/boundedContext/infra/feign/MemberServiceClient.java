package com.mossy.boundedContext.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "member", url = "http://localhost:8082")
public interface MemberServiceClient {

    @PostMapping("/internal/members/verify")
    MemberVerifyResponse verify(@RequestBody MemberVerifyRequest request);
}
