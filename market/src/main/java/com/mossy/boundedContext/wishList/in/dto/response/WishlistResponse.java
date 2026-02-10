package com.mossy.boundedContext.wishList.in.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record WishlistResponse(
        Long wishlistId,
        Long productId,
        String productName,
        String categoryName,
        BigDecimal price,
        String thumbnailUrl
) {
}
