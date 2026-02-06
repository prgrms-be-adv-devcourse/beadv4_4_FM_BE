package com.mossy.shared.market.event;

import com.mossy.shared.market.enums.PayMethod;

import java.math.BigDecimal;

public record OrderCancelEvent(
    String orderNo,
    Long buyerId,
//    BigDecimal amount,
//    PayMethod payMethod,
    String cancelReason
) {

}
