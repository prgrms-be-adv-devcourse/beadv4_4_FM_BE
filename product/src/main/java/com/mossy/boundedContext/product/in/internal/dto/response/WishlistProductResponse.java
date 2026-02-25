package com.mossy.boundedContext.product.in.internal.dto.response;

import java.math.BigDecimal;

public record WishlistProductResponse(
        Long productItemId,
        String productName,
        String categoryName,
        BigDecimal totalPrice,
        String thumbnailUrl
) {}
