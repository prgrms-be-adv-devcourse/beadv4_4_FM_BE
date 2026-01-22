package backend.mossy.shared.cash.dto.request;

import backend.mossy.boundedContext.cash.domain.user.UserEventType;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record UserBalanceRequestDto(
    Long userId,
    BigDecimal amount,
    UserEventType eventType,
    String relTypeCode,
    Long relId
) {

    public UserBalanceRequestDto withUserId(Long userId) {
        return UserBalanceRequestDto.builder()
            .userId(userId)
            .amount(amount)
            .eventType(eventType)
            .relTypeCode(relTypeCode)
            .relId(relId)
            .build();
    }

    public UserBalanceRequestDto {
        if (userId == null) throw new IllegalArgumentException("구매자 ID는 필수입니다.");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("금액은 0보다 커야 합니다.");
        }
    }
}