package com.mossy.boundedContext.payment.in.dto.response;

public record TossPaymentResponse(
    String paymentKey,
    String orderId,
    String orderName,
    String status,
    String method,
    long totalAmount,
    String requestedAt,
    String approvedAt,
    TossCard cancel
) {
    public record TossCard(
        String company,
        String number,
        String cardType
    ) {}
}