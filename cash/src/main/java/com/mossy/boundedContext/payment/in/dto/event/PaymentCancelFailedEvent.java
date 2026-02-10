package com.mossy.boundedContext.payment.in.dto.event;

public record PaymentCancelFailedEvent(
    String paymentKey,
    String reason
) {
}
