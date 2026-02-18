package com.mossy.boundedContext.recommendation.app.usecase;

import com.mossy.boundedContext.recommendation.domain.RecommendPolicy;
import com.mossy.boundedContext.recommendation.out.RecommendItemRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendSearchItemsUseCase {

    private final RecommendItemRepository itemRepository;

    public Mono<List<Long>> searchSimilarProductIds(Long productId) {
        return itemRepository.existsByProductId(productId)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new DomainException(ErrorCode.ITEM_NOT_FOUND));
                }
                return itemRepository.findTopSimilarProductIds(productId, RecommendPolicy.SIMILAR_ITEMS_TOP_N)
                    .collectList();
            });
    }
}