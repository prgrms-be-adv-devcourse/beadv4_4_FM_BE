package com.mossy.member.in.dto.response;

public record SellerApproveResponse(
        Long sellerId,
        String accessToken,
        String refreshToken
) {
}
