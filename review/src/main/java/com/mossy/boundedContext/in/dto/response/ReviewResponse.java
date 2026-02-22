package com.mossy.boundedContext.in.dto.response;

import com.mossy.boundedContext.domain.Review;
import com.mossy.shared.review.enums.ReviewStatus;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long orderItemId,
        Long productId,
        Long userId,
        String content,
        int rating,
        ReviewStatus status,
        LocalDateTime createdAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getOrderItemId(),
                review.getProductId(),
                review.getUserId(),
                review.getContent(),
                review.getRating(),
                review.getStatus(),
                review.getCreatedAt()
        );
    }
}
