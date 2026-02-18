package com.mossy.boundedContext.recommendation.in;

import com.mossy.boundedContext.recommendation.app.RecommendFacade;
import com.mossy.boundedContext.recommendation.out.dto.response.MarketProductResponse;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "Recommendation", description = "AI 추천 API")
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class ApiV1RecommendController {

    private final RecommendFacade recommendFacade;

    @Operation(summary = "유사 추천 상품 조회", description = "해당 상품의 벡터 유사도 기반으로 추천 상품 목록을 조회합니다.")
    @GetMapping("/{productId}")
    public Mono<RsData<List<MarketProductResponse>>> getRecommendations(@PathVariable Long productId) {
        return recommendFacade.searchRecommendations(productId)
            .map(products -> RsData.success(SuccessCode.RECOMMENDATION_FOUND, products));
    }
}