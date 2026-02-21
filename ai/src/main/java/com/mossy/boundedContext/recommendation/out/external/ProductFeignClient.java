package com.mossy.boundedContext.recommendation.out.external;

import com.mossy.boundedContext.recommendation.out.external.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product", url = "http://localhost:8090")
public interface ProductFeignClient {

    @PostMapping("/api/v1/internal/products/filter-by-reviews")
    List<ProductResponse> filterByReviews(@RequestBody List<Long> productIds);

    @PostMapping("/api/v1/internal/products/details")
    List<ProductResponse> getProductDetails(@RequestBody List<Long> productIds);
}
