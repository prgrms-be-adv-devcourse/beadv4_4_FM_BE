package com.mossy.boundedContext.in.dto.response;

public record SellerApproveResponse(
        Long sellerId,
        String accessToken,
        String refreshToken
) {
}
