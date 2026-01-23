package backend.mossy.shared.market.dto.toss;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import java.math.BigDecimal;

public record PaymentCancelTossRequestDto(
    String orderId,
    String paymentKey,
    BigDecimal cancelAmount,
    String cancelReason
) {
    public PaymentCancelTossRequestDto {
        if (orderId == null || orderId.isBlank()) {
            throw new DomainException(ErrorCode.ORDER_ID_REQUIRED);
        }
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new DomainException(ErrorCode.PAYMENT_KEY_REQUIRED);
        }
        if (cancelAmount == null || cancelAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(ErrorCode.CANCEL_AMOUNT_MUST_BE_POSITIVE);
        }
        if (cancelReason == null || cancelReason.isBlank()) {
            throw new DomainException(ErrorCode.CANCEL_REASON_REQUIRED);
        }
    }
}