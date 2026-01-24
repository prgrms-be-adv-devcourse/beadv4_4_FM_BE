package backend.mossy.shared.market.event;

import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import java.math.BigDecimal;

public record OrderCancelEvent(
    String orderNo,
    Long buyerId,
    BigDecimal amount,
    PayMethod payMethod,
    String cancelReason
) {

}
