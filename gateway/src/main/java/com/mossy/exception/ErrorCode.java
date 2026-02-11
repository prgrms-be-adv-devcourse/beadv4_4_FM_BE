package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseCode {

    // ========================================
    // 401 Unauthorized (인증 실패)
    // ========================================
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "만료된 토큰입니다."),
    TOKEN_SIGNATURE_ERROR(401, "토큰 서명이 일치하지 않습니다."),
    AUTH_REQUIRED(401, "로그인이 필요한 서비스입니다."),

    // ========================================
    // 403 Forbidden (권한 없음)
    // ========================================
    ACCESS_DENIED(403, "해당 리소스에 접근할 권한이 없습니다."),
    IP_BLOCKED(403, "접근이 차단된 IP 주소입니다."),

    // ========================================
    // 429 Too Many Requests (트래픽 제한 - 필요한 경우)
    // ========================================
    TOO_MANY_REQUESTS(429, "너무 많은 요청이 발생했습니다. 잠시 후 다시 시도해주세요."),

    // ========================================
    // 500 & 503 (시스템/라우팅 오류)
    // ========================================
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(503, "현재 서비스를 이용할 수 없습니다. (대상 서비스 응답 없음)");

    private final int status;
    private final String msg;
}