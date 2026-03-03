package com.mossy.boundedContext.order.in.dto.request;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreatedRequest(
        String buyerAddress,
        BigDecimal totalPrice,
        String paymentType,
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            Long productItemId,
            Long sellerId,
            int quantity,
            BigDecimal weight,
            BigDecimal price,
            Long userCouponId
    ) {}
}
