package com.mossy.filter;

import com.mossy.security.jwt.JwtProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static java.rmi.server.LogStream.log;

@Slf4j
@Component("JwtAuthenticationFilter")
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
    }

    @Data
    public static class Config {
        private boolean preLogger;
        private boolean postLogger;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            log(">>>>>> Gateway Filter Access: " + exchange.getRequest().getPath());

            ServerHttpRequest request = exchange.getRequest();

            // 1. Authorization 헤더가 아예 없는 경우 차단
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            // 2. Bearer 형식이 아닌 경우 차단
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Invalid token format", HttpStatus.UNAUTHORIZED);
            }

            String jwt = authHeader.substring(7);

            try {
                // 3. parseClaims(jwt)가 실행될 때 만료/변조된 토큰은 예외를 던집니다.
                var claims = jwtProvider.parseClaims(jwt);
                Long userId = Long.valueOf(claims.getSubject());

                // 4. 안전하게 변조된 헤더 주입 (기존 X-User-Id는 삭제/덮어쓰기)
                var requestBuilder = request.mutate()
                        .header("X-User-Id", userId.toString());

                // 5. sellerId가 토큰에 포함되어 있으면 X-Seller-Id 헤더도 추가
                Long sellerId = jwtProvider.getSellerId(claims);
                if (sellerId != null) {
                    requestBuilder.header("X-Seller-Id", sellerId.toString());
                }

                // 6. role이 토큰에 포함되어 있으면 X-User-Role 헤더도 추가
                String role = claims.get("role", String.class);
                if (role != null) {
                    requestBuilder.header("X-User-Role", role);
                }

                ServerHttpRequest mutatedRequest = requestBuilder.build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                // 토큰이 만료되었거나(ExpiredJwtException), 가짜 숫자를 넣었을 때 여기서 잡힙니다.
                System.out.println("토큰 검증 실패: " + e.getMessage());
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}