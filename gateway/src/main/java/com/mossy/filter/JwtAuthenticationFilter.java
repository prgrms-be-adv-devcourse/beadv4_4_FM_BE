package com.mossy.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mossy.security.jwt.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component("JwtAuthenticationFilter")
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, ObjectMapper objectMapper) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
    }

    @Data
    public static class Config {
        private boolean preLogger;
        private boolean postLogger;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            log.debug(">>>>>> Gateway Filter Access: {}", exchange.getRequest().getPath());

            ServerHttpRequest request = exchange.getRequest();

            // 1. Authorization 헤더가 아예 없는 경우 차단
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "NO_AUTH_HEADER", "인증 헤더가 없습니다.", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            // 2. Bearer 형식이 아닌 경우 차단
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "INVALID_TOKEN_FORMAT", "토큰 형식이 잘못되었습니다.", HttpStatus.UNAUTHORIZED);
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

            } catch (ExpiredJwtException e) {
                // 토큰 만료 - 프론트엔드에서 로그인 화면으로 리다이렉트
                log.warn("토큰 만료: {}", e.getMessage());
                return onError(exchange, "TOKEN_EXPIRED", "토큰이 만료되었습니다. 다시 로그인해주세요.", HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                // 그 외 토큰 검증 실패 (변조, 잘못된 형식 등)
                log.error("토큰 검증 실패: {}", e.getMessage());
                return onError(exchange, "INVALID_TOKEN", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    //에러 응답을 JSON 형태로 반환
    private Mono<Void> onError(ServerWebExchange exchange, String errorCode, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorResponse = Map.of(
                "resultCode", "F-" + httpStatus.value(),
                "errorCode", errorCode,
                "msg", message,
                "timestamp", java.time.Instant.now().toString()
        );

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("JSON 응답 생성 실패", e);
            return response.setComplete();
        }
    }
}