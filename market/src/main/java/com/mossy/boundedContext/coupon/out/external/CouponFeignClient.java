package com.mossy.boundedContext.coupon.out.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "coupon-product", url = "http://localhost:8090")
public interface CouponFeignClient {

    @GetMapping("/internal/v1/products/validate-owner")
    void validateProductOwner(
            @RequestParam("productItemId") Long productItemId,
            @RequestParam("sellerId") Long sellerId
    );
}
