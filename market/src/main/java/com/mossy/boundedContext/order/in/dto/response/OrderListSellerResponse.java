package com.mossy.boundedContext.order.in.dto.response;

import com.mossy.shared.market.enums.OrderState;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderListSellerResponse(
        Long orderDetailId,
        Long productId,
        int quantity,
        BigDecimal orderPrice,
        OrderState state,
        LocalDateTime createdAt
) {
}