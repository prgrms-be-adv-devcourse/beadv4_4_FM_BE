package backend.mossy.shared.market.event;

import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import java.math.BigDecimal;

public record PaymentRefundEvent(
    Long orderId,
    Long buyerId,
    BigDecimal amount,
    PayMethod payMethod
) {

}
