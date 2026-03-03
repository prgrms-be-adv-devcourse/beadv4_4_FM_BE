package com.mossy.boundedContext.order.in.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        Long productId,
        String productName,
        BigDecimal price,
        String thumbnailUrl,
        Long categoryId,
        int quantity,
        BigDecimal orderPrice
) { }