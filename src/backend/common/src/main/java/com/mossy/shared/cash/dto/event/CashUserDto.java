package com.mossy.shared.cash.dto.event;

import com.mossy.shared.member.domain.user.UserStatus;
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
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}