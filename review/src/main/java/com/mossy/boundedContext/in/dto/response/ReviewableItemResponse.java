package com.mossy.boundedContext.in.dto.response;

import com.mossy.boundedContext.domain.ReviewableItem;
import com.mossy.boundedContext.out.external.dto.ProductInfoResponse;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewableItemResponse(
        Long orderItemId,
        Long productId,
        String productName,
        String imageUrl,
        Long sellerId,
        LocalDateTime createdAt
) {
    public static ReviewableItemResponse from(ReviewableItem item, ProductInfoResponse productInfo) {
        return ReviewableItemResponse.builder()
                .orderItemId(item.getOrderItemId())
                .productId(item.getProductId())
                .productName(productInfo != null ? productInfo.productName() : null)
                .imageUrl(productInfo != null ? productInfo.thumbnailUrl() : null)
                .sellerId(item.getSellerId())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
