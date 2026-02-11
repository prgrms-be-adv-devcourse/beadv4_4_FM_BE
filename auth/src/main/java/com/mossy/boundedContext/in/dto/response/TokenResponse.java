package com.mossy.boundedContext.in.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
