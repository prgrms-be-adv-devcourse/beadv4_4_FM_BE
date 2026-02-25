package com.mossy.boundedContext.product.in.internal.dto.response;

import java.math.BigDecimal;

public record ProductInfoResponse(
        Long productId,
        Long sellerId,
        String productName,
        String categoryName,
        BigDecimal price,
        String thumbnailUrl,
        Integer quantity
) { }
