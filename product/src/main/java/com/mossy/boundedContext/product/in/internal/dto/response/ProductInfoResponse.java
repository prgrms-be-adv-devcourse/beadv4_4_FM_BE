package com.mossy.boundedContext.product.in.internal.dto.response;

import java.math.BigDecimal;

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
