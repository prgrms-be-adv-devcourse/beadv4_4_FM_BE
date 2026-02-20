package com.mossy.boundedContext.in.dto;

import com.mossy.shared.member.domain.enums.SellerRequestStatus;

import java.util.List;

public record UserInfoDto(
        Long userId,
        String nickname,
        String username,
        SellerRequestStatus status,
        List<String> providers
) {
}
