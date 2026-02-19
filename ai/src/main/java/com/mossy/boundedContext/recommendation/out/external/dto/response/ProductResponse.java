package com.mossy.boundedContext.recommendation.out.external.dto.response;

public record ProductResponse(
    Long productId,
    String name,
    String categoryName,
    Long price,
    String thumbnailUrl,
    int reviewCount,
    double averageRating
) {
}
