package com.mossy.boundedContext.recommendation.out;

import com.mossy.boundedContext.recommendation.domain.RecommendItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecommendItemRepository extends ReactiveCrudRepository<RecommendItem, Long> {

    @Query("""
        SELECT ri.product_id
        FROM recommend_item ri
        WHERE ri.product_id != :productId
        ORDER BY ri.vector_data <=> (
            SELECT vector_data FROM recommend_item WHERE product_id = :productId
        )
        LIMIT :limitCount
        """)
    Flux<Long> findTopSimilarProductIds(Long productId, int limitCount);

    Mono<Boolean> existsByProductId(Long productId);
}
