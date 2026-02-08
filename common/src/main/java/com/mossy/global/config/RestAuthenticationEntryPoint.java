package com.mossy.global.config;

import com.mossy.global.exception.BaseErrorCode;
import com.mossy.global.rsData.RsData;
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

    //스프링 부트 4버전과 jackson 3 + jackson 2을 혼합해서 사용시 꼬여서 Bean으로 만들지 못해서 급하게 이렇게 밖에 못함
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

        Object attr = request.getAttribute("AUTH_ERROR");
        BaseErrorCode errorCode;

        if (attr instanceof BaseErrorCode) {
            errorCode = (BaseErrorCode) attr;
        } else {
            errorCode = new BaseErrorCode() {
                @Override public int getStatus() { return 401; }
                @Override public String getMsg() { return "인증에 실패했습니다."; }
                @Override public String getCode() { return "F-401";}
            };
        }

        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        RsData <Object> body = RsData.fail(errorCode);
        objectMapper.writeValue(response.getWriter(), body);

    }
}
