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
            BigDecimal refundAmount
    ){}
}
