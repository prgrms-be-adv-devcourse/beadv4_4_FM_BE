
package backend.mossy.shared.cash.event;

public record PaymentCompletedEvent(
    Long orderId
) {
}
