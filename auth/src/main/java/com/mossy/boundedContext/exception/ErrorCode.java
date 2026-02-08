package com.mossy.boundedContext.exception;

import com.mossy.global.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {

    // ========================================
    // 400 Bad Request (회원가입/유효성 검증)
    // ========================================
    INVALID_REQUEST(400, "잘못된 요청입니다."),
    DUPLICATE_EMAIL(400, "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(400, "이미 존재하는 닉네임입니다."),
    INVALID_USER_DATA(400, "전달된 사용자 정보가 유효하지 않습니다."),

    // ========================================
    // 401 Unauthorized (인증 실패)
    // ========================================
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "만료된 토큰입니다."),
    TOKEN_SIGNATURE_ERROR(401, "토큰 서명이 일치하지 않습니다."),
    INVALID_CREDENTIALS(401, "이메일 또는 비밀번호가 일치하지 않습니다."),
    ACCOUNT_DISABLED(401, "탈퇴했거나 계정이 정지된 회원입니다."),

    // ========================================
    // 403 Forbidden (권한 부족)
    // ========================================
    ACCESS_DENIED(403, "해당 리소스에 접근할 권한이 없습니다."),

    // ========================================
    // 404 Not Found (대상 없음)
    // ========================================
    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    ROLE_NOT_FOUND(404, "권한 정보를 찾을 수 없습니다.");

    private final int status;
    private final String msg;

    @Override
    public String getCode() {
        return this.name();
    }
}