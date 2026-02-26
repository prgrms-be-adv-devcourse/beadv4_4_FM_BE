package com.mossy.boundedContext.in.dto.response;

import com.mossy.boundedContext.domain.Review;
import com.mossy.boundedContext.out.external.dto.ProductInfoResponse;
import com.mossy.shared.review.enums.ReviewStatus;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long orderItemId,
        Long productId,
        String productName,
        String imageUrl,
        Long userId,
        String content,
        int rating,
        ReviewStatus status,
        LocalDateTime createdAt
) {
    public static ReviewResponse from(Review review, ProductInfoResponse productInfo) {
        return new ReviewResponse(
                review.getId(),
                review.getOrderItemId(),
                review.getProductId(),
                productInfo != null ? productInfo.productName() : null,
                productInfo != null ? productInfo.thumbnailUrl() : null,
                review.getUserId(),
                review.getContent(),
                review.getRating(),
                review.getStatus(),
                review.getCreatedAt()
        );
    }
}
