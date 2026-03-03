package com.mossy.boundedContext.out;

import com.mossy.boundedContext.domain.ReviewableItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewableItemRepository extends JpaRepository<ReviewableItem, Long> {
    boolean existsByOrderItemId(Long orderItemId);
    Optional<ReviewableItem> findByOrderItemId(Long orderItemId);
    List<ReviewableItem> findByBuyerIdAndReviewedFalseOrderByCreatedAtDesc(Long buyerId);
}
