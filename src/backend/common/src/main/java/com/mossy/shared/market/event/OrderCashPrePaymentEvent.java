package com.mossy.shared.market.event;

import com.mossy.shared.cash.dto.request.UserBalanceRequestDto;
import com.mossy.shared.cash.enums.UserEventType;

import java.math.BigDecimal;

public record OrderCashPrePaymentEvent(
    Long orderId,
    Long buyerId,
    BigDecimal amount
){
    public UserBalanceRequestDto toUserBalanceRequestDto() {
        return UserBalanceRequestDto.builder()
            .userId(this.buyerId)
            .amount(this.amount)
            .eventType(UserEventType.사용__주문결제)
            .relTypeCode("ORDER")
            .relId(this.orderId)
            .build();
    }
}
