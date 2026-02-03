package com.mossy.boundedContext.auth.in.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
