package com.mossy.boundedContext.out.external.dto;

import java.math.BigDecimal;

public record ProductInfoResponse(
        Long productId,
        Long sellerId,
        String productName,
        String categoryName,
        BigDecimal price,
        String thumbnailUrl,
        Integer quantity
) {
}
