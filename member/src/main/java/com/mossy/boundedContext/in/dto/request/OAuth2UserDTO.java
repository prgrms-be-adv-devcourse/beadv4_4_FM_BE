package com.mossy.boundedContext.in.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record OAuth2UserDTO(
        @JsonProperty("provider_id")
        String providerId,
        String provider,
        String email,
        String name
) {
}

