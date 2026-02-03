package com.mossy.boundedContext.auth.in.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
