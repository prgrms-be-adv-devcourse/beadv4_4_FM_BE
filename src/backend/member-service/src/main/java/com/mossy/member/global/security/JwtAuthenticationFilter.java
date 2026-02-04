package com.mossy.member.global.security;

import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import com.mossy.global.security.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;
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

            // JwtProvider의 기능을 활용하여 한 줄로 정리
            Long sellerId = jwtProvider.getSellerId(claims);

            UserDetailsImpl base = userDetailsService.loadUserById(userId);
            UserDetailsImpl principal = new UserDetailsImpl(base.getUser(), sellerId);

            if (!principal.isEnabled()) {
                throw new DomainException(ErrorCode.ACCOUNT_DISABLED);
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            handleAuthError(request, response, ErrorCode.EXPIRED_TOKEN);
            return;
        } catch (SignatureException e) {
            handleAuthError(request, response, ErrorCode.TOKEN_SIGNATURE_ERROR);
            return;
        } catch (MalformedJwtException e) {
            handleAuthError(request, response, ErrorCode.INVALID_TOKEN);
            return;
        } catch (DomainException e) {
            handleAuthError(request, response, e.getErrorCode());
            return;
        } catch (Exception e) {
            log.error("JWT 인증 중 알 수 없는 예외 발생", e);
            handleAuthError(request, response, ErrorCode.INVALID_TOKEN);
            return;
        }
    }

    private void handleAuthError(
            HttpServletRequest request,
            HttpServletResponse response,
            ErrorCode errorCode
    ) throws IOException, ServletException {

        SecurityContextHolder.clearContext();
        request.setAttribute("AUTH_ERROR", errorCode);

        authenticationEntryPoint.commence(
                request,
                response,
                new BadCredentialsException(errorCode.name())
        );

    }
}
