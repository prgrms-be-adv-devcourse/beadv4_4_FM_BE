package com.mossy.boundedContext.cash.in.dto.common;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CashUserDto(
    Long id,
    String email,
    String name,
    String address,
    String nickname,
    String profileImage,
    String status,
    BigDecimal latitude,
    BigDecimal longitude,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}