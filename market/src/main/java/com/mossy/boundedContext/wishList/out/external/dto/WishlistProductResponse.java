package com.mossy.boundedContext.wishList.out.external.dto;

import java.math.BigDecimal;

public record WishlistProductResponse(
        Long productId,
        String productName,
        String categoryName,
        BigDecimal price,
        String thumbnailUrl
) {}
