package com.mossy.shared.member.payload;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.mossy.shared.member.domain.enums.UserStatus;
import lombok.Builder;

@Builder
public record UserPayload(
    Long id,
    String email,
    String name,
    String address,
    String nickname,
    String profileImage,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    BigDecimal latitude,
    BigDecimal longitude
) {

}