package com.mossy.shared.cash.dto.event;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record CashUserDto(
    Long id,
    String email,
    String name,
    String rrnEncrypted,
    String phoneNum,
    String password,
    String address,
    String nickname,
    String profileImage,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}