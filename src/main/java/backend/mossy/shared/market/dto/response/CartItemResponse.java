package backend.mossy.shared.market.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CartItemResponse(
        Long productId,
        String productName,
        Long categoryId,
        BigDecimal price,
        String thumbnailUrl,
        Integer quantity
) { }