package com.mossy.filter;

import com.mossy.security.jwt.JwtProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

// (비로그인 + 로그인 모두 허용하는 공개 API에서 유저 식별용)
@Slf4j
@Component("OptionalJwtAuthenticationFilter")
public class OptionalJwtAuthenticationFilter extends AbstractGatewayFilterFactory<OptionalJwtAuthenticationFilter.Config> {

    private final JwtProvider jwtProvider;

    public OptionalJwtAuthenticationFilter(JwtProvider jwtProvider) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
    }

    @Data
    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 내부 헤더 스푸핑 방어: 기존 내부 헤더 제거
            var requestBuilder = request.mutate()
                .headers(headers -> {
                    headers.remove("X-User-Id");
                    headers.remove("X-Seller-Id");
                    headers.remove("X-User-Role");
                });

            // Authorization 헤더가 있으면 파싱 시도
            if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String jwt = authHeader.substring(7);

                    if (!jwt.isBlank()) {
                        try {
                            var claims = jwtProvider.parseClaims(jwt);
                            Long userId = Long.valueOf(claims.getSubject());
                            requestBuilder.header("X-User-Id", userId.toString());

                            Long sellerId = jwtProvider.getSellerId(claims);
                            if (sellerId != null) {
                                requestBuilder.header("X-Seller-Id", sellerId.toString());
                            }

                            String role = claims.get("role", String.class);
                            if (role != null) {
                                requestBuilder.header("X-User-Role", role);
                            }

                            log.debug("Optional JWT 인증 성공: userId={}", userId);
                        } catch (Exception e) {
                            log.debug("Optional JWT 인증 실패 (무시): {}", e.getMessage());
                            // 토큰이 유효하지 않아도 통과
                        }
                    }
                }
            }

            ServerHttpRequest mutatedRequest = requestBuilder.build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }
}

