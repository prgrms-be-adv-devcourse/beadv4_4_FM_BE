package com.mossy.boundedContext.in.dto;

public record LoginRequest(
        String email,
        String password
) { }
