package com.mossy.filter;

import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

 //내부 헤더를 직접 주입하는 스푸핑을 방어하기 위한 필터
@Component("StripInternalHeadersFilter")
public class StripInternalHeadersFilter extends AbstractGatewayFilterFactory<StripInternalHeadersFilter.Config> {

    public StripInternalHeadersFilter() {
        super(Config.class);
    }

    @Data
    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .headers(headers -> {
                        headers.remove("X-User-Id");
                        headers.remove("X-Seller-Id");
                        headers.remove("X-User-Role");
                    })
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        };
    }
}

