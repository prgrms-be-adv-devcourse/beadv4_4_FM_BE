package com.mossy.shared.cash.event;

import com.mossy.shared.cash.enums.PayMethod;

import java.math.BigDecimal;

public record PaymentRefundEvent(
    Long orderId,
    Long buyerId,
    BigDecimal amount,
    PayMethod payMethod
) {

}
