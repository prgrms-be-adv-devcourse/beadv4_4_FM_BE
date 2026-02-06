package com.mossy.boundedContext.cash.in.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SellerCashLogResponseDto(
    Long id,
    String eventType,
    BigDecimal amount,
    BigDecimal balance,
    LocalDateTime createdAt
) {
}
