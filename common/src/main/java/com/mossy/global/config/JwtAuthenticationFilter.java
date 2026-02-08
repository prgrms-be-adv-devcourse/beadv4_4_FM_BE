package com.mossy.global.config;

import com.mossy.global.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        //토큰이 없는 경우
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            Claims claims = jwtProvider.parseClaims(token);
            Long userId = Long.valueOf(claims.getSubject());
            String email = claims.getSubject();
            String role = claims.get("role", String.class);
            Long sellerId = jwtProvider.getSellerId(token);


            String nickname = claims.get("nickname", String.class);
            String name = claims.get("name", String.class);

            UserDetailsImpl principal = new UserDetailsImpl(
                    userId,
                    claims.getSubject(),
                    null,
                    nickname,
                    name,
                    List.of(role),
                    sellerId,
                    true
            );

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            handleAuthError(request, response, "EXPIRED_TOKEN");
        } catch (MalformedJwtException e) {
            handleAuthError(request, response, "INVALID_TOKEN");
        } catch (Exception e) {
            log.error("JWT 인증 중 알 수 없는 예외 발생", e);
            handleAuthError(request, response, "AUTH_FAILURE");
        }
    }

    private void handleAuthError(
            HttpServletRequest request,
            HttpServletResponse response,
            String errorType
    ) throws IOException, ServletException {

        SecurityContextHolder.clearContext();
        request.setAttribute("AUTH_ERROR", errorType);

        authenticationEntryPoint.commence(
                request,
                response,
                new BadCredentialsException(errorType)
        );

    }
}
