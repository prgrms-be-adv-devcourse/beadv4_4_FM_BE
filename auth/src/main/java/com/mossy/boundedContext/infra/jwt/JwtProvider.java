package com.mossy.boundedContext.infra.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final JwtProperties jwtProperties;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, String role, Long sellerId) {
        return createToken(userId, role, sellerId, jwtProperties.accessTokenExpireMs());
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, null,null, jwtProperties.refreshTokenExpireMs());
    }

    public String createToken(Long userId, String role, Long sellerId, long expireMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireMs);

        var builder = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key);

        if (role != null) {
            builder.claim("role", role);
        }

        if (sellerId != null) {
            builder.claim("seller_id", sellerId);
        }

        return builder.compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public Long getSellerId(String token) {
        Claims claims = parseClaims(token);
        Object raw = claims.get("sellerId");
        if (raw == null) return null;
        return (raw instanceof Number) ? ((Number) raw).longValue() : Long.valueOf(raw.toString());
    }

    public boolean verifyToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getRemainingTime(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

}


