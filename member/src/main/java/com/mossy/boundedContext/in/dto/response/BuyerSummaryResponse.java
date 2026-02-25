package com.mossy.boundedContext.in.dto.response;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.shared.member.domain.enums.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BuyerSummaryResponse(
        Long userId,
        String email,
        String name,
        String nickname,
        String profileImage,
        UserStatus status,
        LocalDateTime createdAt
) {
    public static BuyerSummaryResponse from(User user) {
        return BuyerSummaryResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
