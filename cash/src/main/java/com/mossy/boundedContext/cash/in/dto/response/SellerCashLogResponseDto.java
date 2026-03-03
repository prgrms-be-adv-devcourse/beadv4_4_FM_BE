package com.mossy.boundedContext.cash.in.dto.response;

import com.mossy.shared.cash.enums.SellerEventType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SellerCashLogResponseDto(
    Long id,
    SellerEventType eventType,
    BigDecimal amount,
    BigDecimal balance,
    LocalDateTime createdAt
) {
}
