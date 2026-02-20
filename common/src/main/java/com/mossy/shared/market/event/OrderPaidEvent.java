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
            BigDecimal orderPrice, // 구매자 실제 결재액
            BigDecimal originalPrice, // 할인 전 원가
            BigDecimal sellerDiscountAmount, // 판매자 부담 할인 금액
            BigDecimal platformDiscountAmount, // 플랫폼 부담 할인 금액
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}