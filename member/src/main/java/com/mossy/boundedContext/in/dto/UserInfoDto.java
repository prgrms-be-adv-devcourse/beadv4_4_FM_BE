package com.mossy.boundedContext.in.dto;

import com.mossy.shared.member.domain.enums.SellerRequestStatus;

import java.util.List;

public record UserInfoDto(
        Long userId,
        String nickname,
        String email,
        String username,
        String profileImage,
        String rrn,
        String phoneNum,
        String address,
        SellerRequestStatus status,
        List<String> providers,
        boolean hasPassword
) {
}
