package backend.mossy.boundedContext.auth.infra.jwt;

import backend.mossy.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

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
            //토큰 파싱 및 검증
            Claims claims = jwtProvider.parseClaims(token);

            Long userId = Long.valueOf(claims.getSubject());
            String role = String.valueOf(claims.get("role"));

            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            //인증 성공 시 Context에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //필터로 진행
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            //토큰 만료 에러 (401)
            setErrorResponse(response, ErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {

        response.setStatus(errorCode.getStatus());
        response.setContentType("application/json;charset=UTF-8");

        String jsonResponse = String.format(
                "{\"status\": %d, \"message\": \"%s\"}",
                errorCode.getStatus(),
                errorCode.getMsg()
        );

        response.getWriter().write(jsonResponse);
    }
}
