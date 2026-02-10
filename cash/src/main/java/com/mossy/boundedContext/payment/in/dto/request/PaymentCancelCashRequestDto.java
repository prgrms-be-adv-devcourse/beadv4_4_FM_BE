package com.mossy.boundedContext.payment.in.dto.request;

import com.mossy.exception.ErrorCode;
import com.mossy.exception.DomainException;
import java.math.BigDecimal;

public record PaymentCancelCashRequestDto(
    String orderId,
    BigDecimal cancelAmount,
    String cancelReason
) {
    public PaymentCancelCashRequestDto {
        if (orderId == null || orderId.isBlank()) {
            throw new DomainException(ErrorCode.ORDER_ID_REQUIRED);
        }
        if (cancelAmount == null || cancelAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(ErrorCode.CANCEL_AMOUNT_MUST_BE_POSITIVE);
        }
        if (cancelReason == null || cancelReason.isBlank()) {
            throw new DomainException(ErrorCode.CANCEL_REASON_REQUIRED);
        }
    }
}
