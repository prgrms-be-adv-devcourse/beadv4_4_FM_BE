package com.mossy.boundedContext.global.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mossy.jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpireMs,
        long refreshTokenExpireMs
) {
}
