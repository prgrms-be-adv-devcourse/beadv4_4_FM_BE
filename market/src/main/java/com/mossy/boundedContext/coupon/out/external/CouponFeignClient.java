package com.mossy.boundedContext.coupon.out.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product", url = "http://localhost:8090")
public interface CouponFeignClient {

    @GetMapping("/api/v1/coupons/products/validate-owner")
    void validateProductOwner(
            @RequestParam("productItemId") Long productItemId,
            @RequestParam("sellerId") Long sellerId
    );
}
