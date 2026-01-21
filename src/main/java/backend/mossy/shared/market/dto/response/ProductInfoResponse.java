package backend.mossy.shared.market.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductInfoResponse(
        Long productId,
        Long sellerId,
        String productName,
        Long categoryId,
        BigDecimal price,
        String thumbnailUrl,
        Integer quantity
) { }