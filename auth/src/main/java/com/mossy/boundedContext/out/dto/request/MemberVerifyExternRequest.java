package com.mossy.boundedContext.out.dto.request;

public record MemberVerifyExternRequest(
        String email,
        String password
) {}
