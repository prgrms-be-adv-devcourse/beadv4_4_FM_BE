package com.mossy.boundedContext.product.in.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductInfoResponse(
        Long productId,
        Long sellerId,
        String productName,
        String categoryName,
        BigDecimal price,
        BigDecimal weight,
        String thumbnailUrl,
        Integer quantity
) { }