package com.mossy.shared.market.dto.response;

import com.mossy.shared.market.enums.PayMethod;
import com.mossy.shared.market.enums.PaymentStatus;
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