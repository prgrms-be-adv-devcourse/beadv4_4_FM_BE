package com.mossy.shared.market.event;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

public record OrderRefundedEvent(
    Long orderId,
    LocalDateTime refundedAt,
    List<RefundItem> refundItems
    )
    {
    public record RefundItem(
            Long orderItemId,
            BigDecimal refundAmount, //구매재 환불액
            BigDecimal buyerPaidAmount // 구매자가 원래 낸 금액( 부분 환불 비율 계산용)
    ){}
}
