package com.mossy.boundedContext.payment.in.dto.request;

import com.mossy.exception.CashErrorCode;
import com.mossy.exception.DomainException;
import java.math.BigDecimal;

public record PaymentCancelTossRequestDto(
    String orderId,
    String paymentKey,
    BigDecimal cancelAmount,
    String cancelReason
) {
    public PaymentCancelTossRequestDto {
        if (orderId == null || orderId.isBlank()) {
            throw new DomainException(CashErrorCode.ORDER_ID_REQUIRED);
        }
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new DomainException(CashErrorCode.PAYMENT_KEY_REQUIRED);
        }
        if (cancelAmount == null || cancelAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(CashErrorCode.CANCEL_AMOUNT_MUST_BE_POSITIVE);
        }
        if (cancelReason == null || cancelReason.isBlank()) {
            throw new DomainException(CashErrorCode.CANCEL_REASON_REQUIRED);
        }
    }
}