package com.mossy.boundedContext.order.in.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderCreatedResponse(
        Long orderId,
        String orderNo,
        BigDecimal totalPrice
) { }