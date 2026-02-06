package com.mossy.boundedContext.in.dto;

import com.mossy.boundedContext.domain.user.User;
import com.mossy.shared.member.payload.UserPayload;

public class UserDtoMapper {

    private UserDtoMapper() {}

    public static UserPayload from(User user) {
        return UserPayload.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .address(user.getAddress())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .build();
    }
}