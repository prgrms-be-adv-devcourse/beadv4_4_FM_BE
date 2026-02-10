package com.mossy.boundedContext.payment.in.dto.request;

import com.mossy.exception.CashErrorCode;
import com.mossy.exception.DomainException;
import java.math.BigDecimal;

import com.mossy.shared.cash.enums.PayMethod;
import lombok.Builder;

@Builder
public record PaymentConfirmCashRequestDto(
    String orderId,  //orderId -> orderNo
    BigDecimal amount,
    PayMethod payMethod
) {
    public PaymentConfirmCashRequestDto {
        if (orderId == null || orderId.isBlank()) {
            throw new DomainException(CashErrorCode.ORDER_ID_REQUIRED);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(CashErrorCode.AMOUNT_MUST_BE_POSITIVE);
        }
        if (payMethod == null) {
            throw new DomainException(CashErrorCode.PAY_METHOD_REQUIRED);
        }
    }
}


