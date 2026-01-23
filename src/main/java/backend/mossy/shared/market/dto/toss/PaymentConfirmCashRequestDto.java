package backend.mossy.shared.market.dto.toss;

import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record PaymentConfirmCashRequestDto(
    String orderId,  //orderId -> orderNo
    BigDecimal amount,
    PayMethod payMethod
) {
    public PaymentConfirmCashRequestDto {
        if (orderId == null || orderId.isBlank()) {
            throw new DomainException(ErrorCode.ORDER_ID_REQUIRED);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(ErrorCode.AMOUNT_MUST_BE_POSITIVE);
        }
        if (payMethod == null) {
            throw new DomainException(ErrorCode.PAY_METHOD_REQUIRED);
        }
    }
}
