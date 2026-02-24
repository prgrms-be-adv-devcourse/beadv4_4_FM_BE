package com.mossy.boundedContext.recommendation.out.repository;

import com.mossy.boundedContext.recommendation.domain.UserProductClick;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserProductClickRepository extends ReactiveCrudRepository<UserProductClick, Long> {

    @Query("""
        INSERT INTO user_product_click (user_id, product_id, click_count)
        VALUES (:userId, :productId, 1)
        ON CONFLICT (user_id, product_id)
        DO UPDATE SET click_count = user_product_click.click_count + 1
        RETURNING *
        """)
    Mono<UserProductClick> upsertClick(Long userId, Long productId);

    Flux<UserProductClick> findByUserIdOrderByClickCountDesc(Long userId);
}

