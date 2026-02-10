package com.mossy.boundedContext.marketUser.in.dto.command;

import com.mossy.shared.member.domain.enums.UserStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record MarketUserDto(
    Long id,
    String email,
    String name,
    String address,
    String nickname,
    String profileImage,
    BigDecimal longitude,
    BigDecimal latitude,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}