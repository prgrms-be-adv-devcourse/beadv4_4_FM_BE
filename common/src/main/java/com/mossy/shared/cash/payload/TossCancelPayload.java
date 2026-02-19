package com.mossy.shared.cash.payload;

import java.math.BigDecimal;
import java.util.List;

public record TossCancelPayload(
    String paymentKey,
    String orderId,
    String status,
    List<Cancel> cancels
) {
    public record Cancel(
        BigDecimal cancelAmount,
        String cancelReason,
        String canceledAt
    ) {
    }
}
