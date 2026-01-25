package backend.mossy.boundedContext.auth.infra.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpireMs,
        long refreshTokenExpireMs
) {
}
