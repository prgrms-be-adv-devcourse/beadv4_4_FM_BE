package com.mossy.shared.market.dto.response;

import com.mossy.shared.market.enums.ProductStatus;

import java.math.BigDecimal;

public record ProductResponse(
        Long productId,
        Long sellerId,
        Long categoryId,
        String name,
        String description,
        BigDecimal weight,
        BigDecimal price,
        Integer quantity,
        ProductStatus status,
        String thumbnail
) {
}