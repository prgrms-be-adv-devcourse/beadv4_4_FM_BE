package com.mossy.boundedContext.recommendation.in;

import com.mossy.boundedContext.recommendation.app.RecommendFacade;
import com.mossy.boundedContext.recommendation.in.dto.request.ChatRecommendRequest;
import com.mossy.boundedContext.recommendation.in.dto.response.RecommendProductResponse;
import com.mossy.boundedContext.recommendation.out.external.dto.response.ProductResponse;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    public Mono<RsData<List<ProductResponse>>> getRecommendations(@PathVariable Long productId) {
        return recommendFacade.searchRecommendations(productId)
            .map(products -> RsData.success(SuccessCode.RECOMMENDATION_FOUND, products));
    }

    @Operation(summary = "개인 맞춤 추천 상품 조회", description = "유저의 상품 클릭 이력 기반으로 개인 맞춤 추천 상품 목록을 조회합니다.")
    @GetMapping("/personal")
    public Mono<RsData<List<ProductResponse>>> getPersonalRecommendations(
            @RequestHeader("X-User-Id") Long userId) {
        return recommendFacade.getPersonalRecommendations(userId)
            .map(products -> RsData.success(SuccessCode.PERSONAL_RECOMMENDATION_FOUND, products));
    }

    @Operation(summary = "AI 챗봇 추천", description = "사용자의 자연어 질문을 기반으로 추천 상품과 추천 사유를 반환합니다.")
    @PostMapping("/chat")
    public Mono<RsData<List<RecommendProductResponse>>> chatRecommend(@RequestBody ChatRecommendRequest request) {
        return recommendFacade.chatRecommend(request.query())
            .map(products -> RsData.success(SuccessCode.CHAT_RECOMMENDATION_FOUND, products));
    }
}
