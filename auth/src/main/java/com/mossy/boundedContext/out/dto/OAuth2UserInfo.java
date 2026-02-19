package com.mossy.boundedContext.out.dto;

public interface OAuth2UserInfo {
    String providerId();
    String provider();
    String email();
    String name();
}
