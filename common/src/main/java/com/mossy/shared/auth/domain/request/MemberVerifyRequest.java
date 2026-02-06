package com.mossy.shared.auth.domain.request;

public record MemberVerifyRequest(
        String email,
        String password
) {}
