package backend.mossy.shared.cash.dto.request;

import backend.mossy.boundedContext.cash.domain.user.UserEventType;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
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
        if (userId == null) throw new DomainException(ErrorCode.USER_ID_REQUIRED);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(ErrorCode.AMOUNT_MUST_BE_POSITIVE);
        }
    }
}