package backend.mossy.shared.market.event;

import java.math.BigDecimal;

public record OrderCashPaymentRequestEvent(
    Long orderId,
    String orderNo,
    Long buyerId,
    BigDecimal amount
) {
}