package com.mossy.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mossy.global.exception.BaseErrorCode;
import com.mossy.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ServerHttpResponse response = exchange.getResponse();

        // 에러 코드 설정 (전략: Request Attribute에서 에러를 찾거나 기본값 사용)
        BaseErrorCode errorCode = exchange.getAttributeOrDefault("AUTH_ERROR", new BaseErrorCode() {
            @Override public int getStatus() { return 401; }
            @Override public String getMsg() { return "인증에 실패했습니다."; }
        });

        response.setStatusCode(HttpStatus.valueOf(errorCode.getStatus()));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        RsData<Object> body = RsData.fail(errorCode);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }
}