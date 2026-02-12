package com.mossy.shared.market.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderPaidEvent(
        Long orderId,
        Long buyerId,
        String buyerName,
        LocalDateTime createdAt,
        List<OrderItem> orderItems
) {
    public record OrderItem(
            Long orderItemId,
            Long sellerId,
            Long productId,
            BigDecimal weight,
            BigDecimal orderPrice,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}