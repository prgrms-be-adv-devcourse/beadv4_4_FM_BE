package backend.mossy.boundedContext.auth.infra.security;

import backend.mossy.global.exception.ErrorCode;
import backend.mossy.global.rsData.RsData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        if (response.isCommitted()) {
            return;
        }

        ErrorCode errorCode = (ErrorCode) request.getAttribute("AUTH_ERROR");
        if (errorCode == null) errorCode = ErrorCode.INVALID_TOKEN;


        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        RsData <Object> body = RsData.fail(errorCode);
        objectMapper.writeValue(response.getWriter(), body);
    }
}
