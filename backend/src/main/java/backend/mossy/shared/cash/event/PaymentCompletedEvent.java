package backend.mossy.shared.cash.event;

import java.time.LocalDateTime;

/**
 * 결제 완료 이벤트
 * Payment 도메인에서 결제가 완료되었을 때 발행
 */
public record PaymentCompletedEvent(
        Long orderId,
        LocalDateTime paymentDate
) {
}
