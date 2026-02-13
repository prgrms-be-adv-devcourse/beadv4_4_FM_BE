package com.mossy.boundedContext.recommendation.app;

import com.mossy.boundedContext.recommendation.app.usecase.RecommendSearchItemsUseCase;
import com.mossy.boundedContext.recommendation.app.usecase.RecommendSyncItemUseCase;
import com.mossy.boundedContext.recommendation.in.dto.request.ProductCreateRequestDto;
import com.mossy.boundedContext.recommendation.out.RecommendFeignClient;
import com.mossy.boundedContext.recommendation.out.dto.response.MarketProductResponse;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.market.event.ProductUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendFacade {

    private final RecommendSyncItemUseCase recommendSyncItemUseCase;
    private final RecommendSearchItemsUseCase recommendSearchItemsUseCase;
    private final RecommendFeignClient recommendFeignClient;

    public Mono<Void> syncItem(ProductCreateRequestDto request) {
        return recommendSyncItemUseCase.syncItem(request);
    }

    public Mono<Void> syncUpdate(ProductUpdatedEvent event) {
        return recommendSyncItemUseCase.syncUpdate(event);
    }

    public Mono<List<MarketProductResponse>> searchRecommendations(Long productId) {
        return recommendSearchItemsUseCase.searchSimilarProductIds(productId)
            .flatMap(productIds ->
                Mono.fromCallable(() -> recommendFeignClient.filterByReviews(productIds))
                    .subscribeOn(Schedulers.boundedElastic())
            )
            .onErrorMap(
                e -> !(e instanceof DomainException),
                e -> new DomainException(ErrorCode.FEIGN_CALL_FAILED)
            );
    }
}