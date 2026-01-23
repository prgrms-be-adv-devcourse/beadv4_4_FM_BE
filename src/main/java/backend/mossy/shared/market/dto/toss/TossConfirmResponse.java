package backend.mossy.shared.market.dto.toss;

import java.math.BigDecimal;

public record TossConfirmResponse(
    String paymentKey,
    String orderId,
    String status,
    BigDecimal totalAmount,
    String method,
    String approvedAt
) {
}
