package com.mossy.boundedContext.product.in.internal.dto.response;

import java.math.BigDecimal;

public record WishlistProductResponse(
        Long productId,
        String productName,
        String categoryName,
        BigDecimal price,
        String thumbnailUrl
) {}
