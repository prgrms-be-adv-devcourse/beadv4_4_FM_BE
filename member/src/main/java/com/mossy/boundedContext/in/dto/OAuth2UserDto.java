package com.mossy.boundedContext.in.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuth2UserDto(
        @JsonProperty("provider_id")
        String providerId,
        String provider,
        String email,
        String name
) {
}

