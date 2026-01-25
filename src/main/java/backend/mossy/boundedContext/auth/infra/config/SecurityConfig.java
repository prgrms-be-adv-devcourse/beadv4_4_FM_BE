package backend.mossy.boundedContext.auth.infra.config;

import backend.mossy.boundedContext.auth.infra.jwt.JwtAuthenticationFilter;
import backend.mossy.boundedContext.auth.infra.security.RestAccessDeniedHandler;
import backend.mossy.boundedContext.auth.infra.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http

                //CSRF 비활성
                .csrf(csrf -> csrf.disable())
                //CORS 설정
                .cors(Customizer.withDefaults())
                //세션 인증 방식 비활성, JWT 사용 방식으로 변경
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //폼 로그인 비활성
                .formLogin(form -> form.disable())
                //기본 인증 비활성
                .httpBasic(basic -> basic.disable())
                // 인증 / 인가 예외 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                //swagger
                                "/mossy-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/error",

                                //Auth 테스트 (토큰)
                                "/api/v1/auth/signup",
                                "/api/v1/auth/login",
                                "/api/v1/auth/reissue",
                                "/api/v1/auth/logout",
                                "/api/v1/auth/ping",

                                //Cash API Check
                                "/api/v1/cash/**",
                               "/api/v1/payments/**",
                                "/api/auth/logout",

                                //Cash API Check
                                "/api/v1/cash/**",
                                "/api/auth/logout",

                                //Cash API Check
                                "/api/v1/cash/**",

                                //Cart API
                                "/api/v1/cart/**",
                                "/api/v1/orders/**",

                                "/api/v1/product/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                //JWT 인증 필터
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
