package com.mossy.boundedContext.out.dto;

import java.util.Map;

public record GoogleUserInfo(Map<String, Object> attributes ) implements OAuth2UserInfo {

    @Override
    public String providerId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String provider() {
        return "google";
    }

    @Override
    public String email() {
        return (String) attributes.get("email");
    }

    @Override
    public String name() {
        return (String) attributes.get("name");
    }
}
