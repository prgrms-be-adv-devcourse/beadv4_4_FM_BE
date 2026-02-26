package com.mossy.boundedContext.payment.in.dto.request;

import com.mossy.exception.ErrorCode;
import com.mossy.exception.DomainException;
import java.math.BigDecimal;
import java.util.List;

public record PaymentCancelCashRequestDto(
    String orderId,
    String cancelReason,
    List<Long> ids,
    BigDecimal cancelAmount
) {
    public PaymentCancelCashRequestDto {
        if (orderId == null || orderId.isBlank()) {
            throw new DomainException(ErrorCode.ORDER_ID_REQUIRED);
        }
        if (cancelReason == null || cancelReason.isBlank()) {
            throw new DomainException(ErrorCode.CANCEL_REASON_REQUIRED);
        }
    }

    public boolean isPartialCancel() {
        return ids != null && !ids.isEmpty();
    }
}
