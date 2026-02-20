package com.mossy.boundedContext.out.external.dto.response;

import com.mossy.boundedContext.domain.user.User;

import java.util.List;

public record SocialLonginResponse(
        Long id,
        String email,
        String name,
        List<String> roles,
        boolean isNewUser
) {
    public static SocialLonginResponse from(User user, boolean isNewUser) {
        List<String> roleNames = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getCode().name())
                .toList();

        return new SocialLonginResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                roleNames,
                isNewUser
        );
    }
}

