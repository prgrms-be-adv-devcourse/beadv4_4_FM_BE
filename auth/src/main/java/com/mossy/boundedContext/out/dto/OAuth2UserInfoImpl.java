package com.mossy.boundedContext.out.dto;

import java.util.Map;

public class OAuth2UserInfoImpl implements OAuth2UserInfo {
    private final Map<String, Object> attributes;
    private final String provider;

    public OAuth2UserInfoImpl(Map<String, Object> attributes, String provider) {
        this.attributes = attributes;
        this.provider = provider;
    }

    @Override
    public String providerId() {
        return switch (provider) {
            case "google" -> String.valueOf(attributes.get("sub"));
            case "kakao" -> String.valueOf(attributes.get("id"));
            default -> null;
        };
    }

    @Override
    public String provider() {
        return provider;
    }

    @Override
    public String email() {
        return switch (provider) {
            case "google" -> (String) attributes.get("email");
            case "kakao" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                if (kakaoAccount != null) {
                    yield (String) kakaoAccount.get("email");
                }
                yield null;
            }
            default -> null;
        };
    }

    @Override
    public String name() {
        return switch (provider) {
            case "google" -> (String) attributes.get("name");
            case "kakao" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> profile = (Map<String, Object>) attributes.get("properties");
                if (profile != null) {
                    yield (String) profile.get("nickname");
                }
                yield null;
            }
            default -> null;
        };
    }
}


