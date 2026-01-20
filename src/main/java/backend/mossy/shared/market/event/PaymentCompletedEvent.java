package backend.mossy.shared.market.event;

public record PaymentCompletedEvent(
    Long orderId
) {
}
