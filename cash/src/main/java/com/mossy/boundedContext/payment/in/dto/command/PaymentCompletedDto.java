package com.mossy.boundedContext.payment.in.dto.command;

import com.mossy.boundedContext.payment.domain.Payment;
import com.mossy.boundedContext.payment.out.dto.response.MarketOrderResponse;
import com.mossy.shared.cash.enums.PayMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PaymentCompletedDto(
    Long orderId,
    Long buyerId,
    LocalDateTime paymentDate,
    BigDecimal amount,
    PayMethod payMethod
) {
    public static PaymentCompletedDto of(
        MarketOrderResponse order,
        Payment payment
    ) {
        return PaymentCompletedDto.builder()
            .orderId(order.orderId())
            .buyerId(order.buyerId())
            .paymentDate(payment.getCreatedAt())
            .amount(order.totalAmount())
            .payMethod(payment.getPayMethod())
            .build();
    }
}