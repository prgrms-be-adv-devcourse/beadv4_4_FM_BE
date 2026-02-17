package com.mossy.boundedContext.cart.out.external;

import com.mossy.boundedContext.cart.out.external.dto.ProductInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product", url = "http://localhost:8090")
public interface CartFeignClient {

    @GetMapping("/api/v1/carts/products")
    List<ProductInfoResponse> findByIds(@RequestParam("productItemIds") List<Long> productItemIds);
}
