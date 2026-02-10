package com.mossy.boundedContext.out.dto.request;

public record MemberVerifyRequest(
        String email,
        String password
) {}
