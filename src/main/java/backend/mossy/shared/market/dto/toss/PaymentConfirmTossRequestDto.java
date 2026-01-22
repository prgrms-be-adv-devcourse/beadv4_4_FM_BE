package backend.mossy.shared.market.dto.toss;

import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import java.math.BigDecimal;
import lombok.Builder;

//PG 최종 승인
@Builder
public record PaymentConfirmTossRequestDto(
    String paymentKey,
    String orderId,  //orderId -> orderNo
    BigDecimal amount,
    PayMethod payMethod
) {
    public PaymentConfirmTossRequestDto {
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("paymentKey는 필수입니다.");
        }
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId(orderNo)는 필수입니다.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
        }
        if (payMethod == null) {
            throw new IllegalArgumentException("결제 수단(payMethod)은 필수 선택 사항입니다.");
        }
    }
}
