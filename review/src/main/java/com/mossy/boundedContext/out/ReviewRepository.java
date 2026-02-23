package com.mossy.boundedContext.out;

import com.mossy.boundedContext.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByOrderItemId(Long orderItemId);
    Optional<Review> findById(Long reviewId);
    Page<Review> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);
}
