package com.mossy.boundedContext.order.in.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderResponse(
        Long orderId,
        String orderNo,
        BigDecimal totalPrice,
        String buyerName,
        String address
) {
}