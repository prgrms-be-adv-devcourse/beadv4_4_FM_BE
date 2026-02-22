package com.mossy.shared.market.event;

import com.mossy.shared.market.enums.CouponType;
import com.mossy.shared.market.enums.IssuerType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderPurchaseConfirmedEvent(
        Long orderId,
        Long buyerId,
        LocalDateTime paidAt,
        List<OrderItemPayload> orderItems
) {
    public record OrderItemPayload(
            Long orderItemId,
            Long sellerId,
            Long productItemId,
            Long userCouponId,
            CouponType couponType,
            IssuerType issuerType,
            BigDecimal weight,
            BigDecimal finalPrice,
            BigDecimal originalPrice,
            BigDecimal discountAmount
    ) {}
}