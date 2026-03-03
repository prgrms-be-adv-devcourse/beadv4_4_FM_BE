package com.mossy.shared.cash.payload;

import java.math.BigDecimal;
import java.util.List;

public record TossCancelPayload(
    String orderId,
    List<Cancel> cancels,
    List<Long> orderItemIds,
    String refundType
) {
    public TossCancelPayload {
        if (orderItemIds == null) {
            orderItemIds = List.of();
        }
    }

    public record Cancel(
        BigDecimal cancelAmount,
        String cancelReason
    ) {
    }
}
