package com.mossy.boundedContext.recommendation.in.dto.response;

public record RecommendProductResponse(
    Long productId,
    String name,
    Long price,
    String imageUrl,
    String aiReason
) {
}
