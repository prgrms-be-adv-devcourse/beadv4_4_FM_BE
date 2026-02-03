package com.mossy.shared.market.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;


@Builder
public record ProductDetailResponse(
        Long productId,
        String name,
        String description,
        BigDecimal price,
        BigDecimal weight,
        Integer quantity,
        String status,
        String categoryName,
        Long sellerId,
        String thumbnail,
        List<String> images
) {
}
