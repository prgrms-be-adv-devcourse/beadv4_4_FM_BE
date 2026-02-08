package com.mossy.boundedContext.in.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}