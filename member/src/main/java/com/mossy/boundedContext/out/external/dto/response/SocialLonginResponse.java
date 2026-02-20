package com.mossy.boundedContext.out.external.dto.response;

public record SocialLonginResponse(
        Long id,
        String email,
        String name,
        java.util.List<String> roles,
        boolean isNewUser
) {
}

