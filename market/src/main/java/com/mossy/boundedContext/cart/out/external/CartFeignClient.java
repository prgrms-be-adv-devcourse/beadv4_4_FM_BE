package com.mossy.boundedContext.cart.out.external;

import com.mossy.boundedContext.cart.out.external.dto.ProductInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "cart-product", url = "${mossy.feign.product-url}")
public interface CartFeignClient {

    @GetMapping("/internal/v1/products")
    List<ProductInfoResponse> findByIds(@RequestParam("productItemIds") List<Long> productItemIds);
}
