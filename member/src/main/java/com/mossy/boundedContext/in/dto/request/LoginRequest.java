package com.mossy.boundedContext.in.dto.request;

public record LoginRequest(
        String email,
        String password
) { }
