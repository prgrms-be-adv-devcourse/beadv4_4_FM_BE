package com.mossy.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;
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
                .signWith(key, SignatureAlgorithm.HS256);

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

    public long getRemainingTime(String token) {
        Date expiration = parseClaims(token).getExpiration();
        long now = new Date().getTime();
        return expiration.getTime() - now;
    }

    public Long getSellerId(Claims claims) {
        Object raw = claims.get("seller_id");
        if (raw == null) return null;
        return (raw instanceof Number) ? ((Number) raw).longValue() : Long.valueOf(raw.toString());
    }

    // 1. 토큰 유효성 검사 (추가)
    public boolean verifyToken(String token) {
        try {
            parseClaims(token); // 내부적으로 parseClaims가 검증까지 수행합니다.
            return true;
        } catch (Exception e) {
            // 토큰 만료, 서명 불일치 등의 경우 false 반환
            return false;
        }
    }

    // 2. 토큰에서 바로 userId(Subject) 추출 (추가)
    public Long getUserId(String token) {
        String subject = parseClaims(token).getSubject();
        return Long.valueOf(subject);
    }

    // 3. 토큰에서 바로 Role 추출 (추가해두면 Gateway 필터에서 쓰기 좋습니다)
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }
}


