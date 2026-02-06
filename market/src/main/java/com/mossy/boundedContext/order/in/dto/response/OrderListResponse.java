package com.mossy.boundedContext.order.in.dto.response;


import com.mossy.shared.market.enums.OrderState;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderListResponse(
        Long orderId,
        String orderNo,
        BigDecimal totalPrice,
        OrderState state,
        Long itemCount,
        String address,
        LocalDateTime createdAt
) {
}