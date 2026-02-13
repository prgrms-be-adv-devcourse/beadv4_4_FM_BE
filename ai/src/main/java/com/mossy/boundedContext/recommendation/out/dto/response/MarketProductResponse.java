package com.mossy.boundedContext.recommendation.out.dto.response;

public record MarketProductResponse(
    Long productId,
    String name,
    String categoryName,
    Long price,
    String thumbnailUrl,
    int reviewCount,
    double averageRating
) {
}
