package com.mossy.boundedContext.payout.in.dto.event;


import com.mossy.shared.member.domain.enums.UserStatus;
import com.mossy.shared.member.payload.UserPayload;
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
    public static PayoutUserDto from(UserPayload user) {
        return PayoutUserDto.builder()
                .id(user.id())
                .email(user.email())
                .name(user.name())
                .address(user.address())
                .nickname(user.nickname())
                .latitude(user.latitude())
                .longitude(user.longitude())
                .profileImage(user.profileImage())
                .status(user.status())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .build();
    }
}
