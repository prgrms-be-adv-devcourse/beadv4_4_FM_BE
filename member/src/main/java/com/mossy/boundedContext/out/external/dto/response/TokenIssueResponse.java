package com.mossy.boundedContext.out.external.dto.response;

public record TokenIssueResponse(
        String accessToken,
        String refreshToken,
        boolean isNewUser
) {}

