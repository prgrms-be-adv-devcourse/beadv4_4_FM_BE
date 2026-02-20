package com.mossy.boundedContext.out.dto.response;

import java.util.List;

public record SocialLonginResponse(
        Long id,
        String email,
        String name,
        List<String> roles,
        boolean isNewUser
) {
}


