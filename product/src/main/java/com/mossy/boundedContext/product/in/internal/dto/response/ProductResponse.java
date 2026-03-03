package com.mossy.boundedContext.product.in.internal.dto.response;

public record ProductResponse(
        Long productId,
        String name,
        String categoryName,
        Long totalPrice,
        String thumbnailUrl,
        int reviewCount,
        double averageRating
) { }
