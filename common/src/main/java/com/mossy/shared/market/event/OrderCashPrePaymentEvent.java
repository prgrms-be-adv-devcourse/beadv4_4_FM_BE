package com.mossy.shared.market.event;

import java.math.BigDecimal;

public record OrderCashPrePaymentEvent(
    Long orderId,
    Long buyerId,
    BigDecimal amount
){
}