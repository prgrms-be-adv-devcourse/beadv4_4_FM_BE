package backend.mossy.shared.market.dto.toss;

import java.math.BigDecimal;

public record PaymentCancelTossRequestDto(
    String orderId,
    String paymentKey,
    BigDecimal cancelAmount,
    String cancelReason
) {
    public PaymentCancelTossRequestDto {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("주문번호(orderId)는 필수입니다.");
        }
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("결제키(paymentKey)는 필수입니다.");
        }
        if (cancelAmount == null || cancelAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("취소 금액은 0보다 커야 합니다.");
        }
        if (cancelReason == null || cancelReason.isBlank()) {
            throw new IllegalArgumentException("취소 사유는 필수입니다.");
        }
    }
}