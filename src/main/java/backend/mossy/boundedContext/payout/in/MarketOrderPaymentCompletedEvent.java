package backend.mossy.boundedContext.payout.in;

import backend.mossy.shared.market.dto.event.OrderDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MarketOrderPaymentCompletedEvent {
    private final OrderDto order;
}
