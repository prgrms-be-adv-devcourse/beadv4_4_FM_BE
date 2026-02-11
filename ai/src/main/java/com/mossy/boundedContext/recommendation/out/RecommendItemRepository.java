package com.mossy.boundedContext.recommendation.out;

import com.mossy.boundedContext.recommendation.domain.RecommendItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RecommendItemRepository extends ReactiveCrudRepository<RecommendItem, Long> {
}