package com.mossy.shared.market.dto.toss;

import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import java.math.BigDecimal;

import com.mossy.shared.market.enums.PayMethod;
import lombok.Builder;

//PG 최종 승인
@Builder
public record PaymentConfirmTossRequestDto(
    String paymentKey,
    String orderId,  //orderId -> orderNo
    BigDecimal amount,
    PayMethod payMethod
) {
    public PaymentConfirmTossRequestDto {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new DomainException(ErrorCode.PAYMENT_KEY_REQUIRED);
        }
        if (orderId == null || orderId.isBlank()) {
            throw new DomainException(ErrorCode.ORDER_ID_REQUIRED);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(ErrorCode.AMOUNT_MUST_BE_POSITIVE);
        }
        if (payMethod == null) {
            throw new DomainException(ErrorCode.PAY_METHOD_REQUIRED);
        }
    }
}
