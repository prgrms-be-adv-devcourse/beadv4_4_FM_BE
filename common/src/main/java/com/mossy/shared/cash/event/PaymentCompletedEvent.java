package com.mossy.shared.cash.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentCompletedEvent(
    Long orderId,
    Long buyerId,
    LocalDateTime paymentDate,
    BigDecimal amount,
    String payMethod
) {
}