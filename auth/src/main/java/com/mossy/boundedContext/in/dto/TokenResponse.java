package com.mossy.boundedContext.in.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
