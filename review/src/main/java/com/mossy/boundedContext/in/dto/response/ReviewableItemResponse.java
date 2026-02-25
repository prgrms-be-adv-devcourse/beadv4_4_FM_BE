package com.mossy.boundedContext.in.dto.response;

import com.mossy.boundedContext.domain.ReviewableItem;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewableItemResponse(
        Long orderItemId,
        Long productId,
        Long sellerId,
        LocalDateTime createdAt
) {
    public static ReviewableItemResponse from(ReviewableItem item) {
        return ReviewableItemResponse.builder()
                .orderItemId(item.getOrderItemId())
                .productId(item.getProductId())
                .sellerId(item.getSellerId())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
