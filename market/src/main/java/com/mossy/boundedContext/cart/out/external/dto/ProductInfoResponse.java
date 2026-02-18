package com.mossy.boundedContext.cart.out.external.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductInfoResponse(
        Long productId,
        Long sellerId,
        String productName,
        String categoryName,
        BigDecimal price,
        String thumbnailUrl,
        Integer quantity
) { }
