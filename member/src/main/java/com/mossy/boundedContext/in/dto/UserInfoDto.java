package com.mossy.boundedContext.in.dto;

import com.mossy.shared.member.domain.enums.SellerRequestStatus;

public record UserInfoDto(
        Long userId,
        String nickname,
        String username,
        SellerRequestStatus status
) {
}
