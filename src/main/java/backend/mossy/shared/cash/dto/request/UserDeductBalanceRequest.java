package backend.mossy.shared.cash.dto.request;

import backend.mossy.boundedContext.cash.domain.user.UserEventType;
import java.math.BigDecimal;

public record UserDeductBalanceRequest(
    Long userId,
    BigDecimal amount,
    UserEventType eventType,
    Long orderId
) {
    public UserDeductBalanceRequest {
        if (userId == null) throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
    }
}
