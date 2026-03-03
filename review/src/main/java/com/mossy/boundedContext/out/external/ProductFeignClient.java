package com.mossy.boundedContext.out.external;

import com.mossy.boundedContext.out.external.dto.ProductInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "review-product", url = "${mossy.feign.product-url}")
public interface ProductFeignClient {

    @GetMapping("/internal/v1/products/carts")
    List<ProductInfoResponse> getProductInfos(@RequestParam("productItemIds") List<Long> productItemIds);
}
