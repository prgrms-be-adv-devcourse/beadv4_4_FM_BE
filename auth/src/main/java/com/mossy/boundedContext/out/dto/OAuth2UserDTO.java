package com.mossy.boundedContext.out.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record OAuth2UserDTO(
        @JsonProperty("provider_id")
        String providerId,
        String provider,
        String email,
        String name
) {
    public static OAuth2UserDTO from(OAuth2UserInfo userInfo) {
        return new OAuth2UserDTO(
                userInfo.providerId(),
                userInfo.provider(),
                userInfo.email(),
                userInfo.name()
        );
    }
}

