package com.mossy.boundedContext.order.in.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderDetailResponse(
        Long productId,
        int quantity,
        BigDecimal orderPrice,
        String sellerName
) {
}