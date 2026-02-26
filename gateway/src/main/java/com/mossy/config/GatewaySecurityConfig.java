package com.mossy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity // WebFlux 전용 보안 어노테이션
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .cors(cors -> cors.disable())
                // 1. CSRF, FormLogin, HttpBasic 비활성화 (Stateless API)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(form -> form.disable())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                // 2. 세션을 사용하지 않도록 설정 (JWT 사용 환경)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

                // 3. 경로별 권한 설정
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/internal/**").denyAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .anyExchange().permitAll()
                )
                .build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // 모든 필터보다 먼저 실행
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(
                "http://mossy-eco.biz",
                "https://mossy-eco.biz",
                "http://localhost:5173",
                "http://localhost:3000"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}