package com.mossy.boundedContext.payment.in.dto.response;

import com.mossy.shared.cash.enums.PayMethod;
import com.mossy.shared.cash.enums.PaymentStatus;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentResponse(
    Long paymentId,
    String paymentKey,
    String orderNo,
    BigDecimal amount,
    PayMethod payMethod,
    PaymentStatus status,
    String failReason,
    LocalDateTime createdAt
) {
}