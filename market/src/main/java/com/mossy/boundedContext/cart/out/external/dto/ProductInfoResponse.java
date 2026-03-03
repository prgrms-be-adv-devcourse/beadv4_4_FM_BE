package com.mossy.boundedContext.cart.out.external.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductInfoResponse(
        Long productItemId,
        Long sellerId,
        String productName,
        String categoryName,
        BigDecimal totalPrice,
        String thumbnailUrl,
        Integer quantity,
        String optionCombination,
        BigDecimal weight
) { }
