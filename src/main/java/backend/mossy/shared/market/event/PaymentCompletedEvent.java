package backend.mossy.shared.market.event;

import backend.mossy.boundedContext.market.domain.order.OrderState;

public record PaymentCompletedEvent(
        Long orderId,
        Long buyerId,
        OrderState state
){ }
