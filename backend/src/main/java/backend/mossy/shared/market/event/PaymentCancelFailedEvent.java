package backend.mossy.shared.market.event;

public record  PaymentCancelFailedEvent(
    String paymentKey,
    String reason
) {
}

