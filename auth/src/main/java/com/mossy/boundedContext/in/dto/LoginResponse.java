package com.mossy.boundedContext.in.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
