package com.mossy.boundedContext.global.jwt;

import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final SecretKey key;
    private final JwtProperties jwtProperties;

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_SELLER_ID = "seller_id";


    public String createAccessToken(Long userId, List<String> roles, Long sellerId) {
        return createToken(userId, roles, sellerId, jwtProperties.accessTokenExpireMs());
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, null, null, jwtProperties.refreshTokenExpireMs());
    }

    public String createToken(Long userId, List<String> roles, Long sellerId, long expireMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireMs);

        var builder = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key);

        if (roles != null && !roles.isEmpty()) {
            builder.claim("roles", roles);
        }

        if (sellerId != null) {
            builder.claim("seller_id", sellerId);
        }

        return builder.compact();
    }

    public Claims parseClaims(String token) {
       try {
           return Jwts.parser()
                   .verifyWith(key)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
       } catch (io.jsonwebtoken.ExpiredJwtException e) {
           throw e;
       } catch (Exception e) {
           throw new DomainException(ErrorCode.INVALID_TOKEN);
       }
    }

    public Long getUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    //AccessToken 인가에 사용
    public String getRole(String token) {
        return parseClaims(token).get(CLAIM_ROLE, String.class);
    }

    //seller 관련 인가
    public Long getSellerId(String token) {
        return parseClaims(token).get(CLAIM_SELLER_ID, Long.class);
    }

    public JwtUserClaim getUserClaim(String token) {
        Claims claims = parseClaims(token);
        return new JwtUserClaim(
                Long.valueOf(claims.getSubject()),
                claims.get(CLAIM_ROLE, String.class),
                claims.get(CLAIM_SELLER_ID, Long.class)
        );
    }

    public boolean verifyToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long verifyRefreshANdGetUserId(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        return Long.valueOf(claims.getSubject());
    }

    public long getRemainingTime(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

}
