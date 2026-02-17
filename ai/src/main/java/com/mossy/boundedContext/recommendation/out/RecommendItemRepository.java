package com.mossy.boundedContext.recommendation.out;

import com.mossy.boundedContext.recommendation.domain.RecommendItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecommendItemRepository extends ReactiveCrudRepository<RecommendItem, Long> {

    // Todo 백터 유사도 검색 쿼리문 작성
    Flux<RecommendItem> findTopSimilarItems(String requestVector, int limit);

    Mono<Boolean> existsByProductId(Long productId);
}