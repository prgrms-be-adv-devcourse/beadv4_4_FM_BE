package com.mossy.boundedContext.cash.in.dto.command;

import com.mossy.shared.member.domain.enums.UserStatus;
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
    UserStatus status,
    BigDecimal latitude,
    BigDecimal longitude,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}