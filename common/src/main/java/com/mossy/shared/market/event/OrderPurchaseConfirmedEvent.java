package com.mossy.shared.market.event;

import com.mossy.shared.market.enums.CouponType;

import java.math.BigDecimal;
import java.util.List;

public record OrderPurchaseConfirmedEvent(
        Long orderId,
        Long buyerId,
        List<OrderItemPayload> orderItems
) {
    public record OrderItemPayload(
            Long orderItemId,
            Long sellerId,
            Long productItemId,
            Long userCouponId,
            CouponType couponType,
            BigDecimal weight,
            BigDecimal finalPrice,
            BigDecimal originalPrice,
            BigDecimal discountAmount
    ) {}
}