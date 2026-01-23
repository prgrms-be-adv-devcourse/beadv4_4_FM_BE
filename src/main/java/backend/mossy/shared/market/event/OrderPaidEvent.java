package backend.mossy.shared.market.event;

public record OrderPaidEvent(
        Long buyerId
) { }