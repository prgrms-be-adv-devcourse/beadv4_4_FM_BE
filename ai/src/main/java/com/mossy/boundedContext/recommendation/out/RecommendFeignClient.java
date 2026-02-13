package com.mossy.boundedContext.recommendation.out;

import com.mossy.boundedContext.recommendation.out.dto.response.MarketProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "market", url = "http://localhost:8081")
public interface RecommendFeignClient {

    @PostMapping("/api/v1/internal/products/filter-by-reviews")
    List<MarketProductResponse> filterByReviews(@RequestBody List<Long> productIds);
}
