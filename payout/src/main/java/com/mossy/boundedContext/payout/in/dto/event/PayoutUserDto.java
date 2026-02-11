package com.mossy.boundedContext.payout.in.dto.event;


import com.mossy.shared.member.domain.enums.UserStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PayoutUserDto(
        Long id,
        String email,
        String name,
        String address,
        String nickname,
        BigDecimal latitude,
        BigDecimal longitude,
        String profileImage,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
