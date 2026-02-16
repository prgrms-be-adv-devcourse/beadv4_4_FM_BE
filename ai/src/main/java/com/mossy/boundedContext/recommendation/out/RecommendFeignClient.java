package com.mossy.boundedContext.recommendation.out;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "market", url = "http://localhost:8081")
public interface RecommendFeignClient {
}