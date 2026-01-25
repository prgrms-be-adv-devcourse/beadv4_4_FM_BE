package backend.mossy.shared.market.event;

import backend.mossy.boundedContext.cash.domain.user.UserEventType;
import backend.mossy.shared.cash.dto.request.UserBalanceRequestDto;
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
