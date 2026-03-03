package com.mossy.boundedContext.payment.in.dto.response;

import java.math.BigDecimal;

public record TossConfirmResponse(
    String paymentKey,
    String orderId,
    String status,
    BigDecimal totalAmount,
    String method,
    String approvedAt
) {
}
