package com.mossy.boundedContext.cash.in.dto.response;

import com.mossy.shared.cash.enums.UserEventType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UserCashLogResponseDto(
    Long id,
    UserEventType eventType,
    BigDecimal amount,
    BigDecimal balance,
    LocalDateTime createdAt
) {
}
