package com.mossy.boundedContext.out.external.dto.request;

public record MemberVerifyRequest(
        String email,
        String password
) {}
