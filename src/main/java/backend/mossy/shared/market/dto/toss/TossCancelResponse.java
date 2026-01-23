package backend.mossy.shared.market.dto.toss;

import java.math.BigDecimal;
import java.util.List;

public record TossCancelResponse(
    String paymentKey,
    String orderId,
    String status,
    List<Cancel> cancels
) {
    public record Cancel(
        BigDecimal cancelAmount,
        String cancelReason,
        String canceledAt
    ) {
    }
}
