package com.mossy.boundedContext.out.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuth2UserDTO(
        @JsonProperty("provider_id")
        String providerId,
        String provider,
        String email,
        String name,
        Long linkUserId
) {
}

