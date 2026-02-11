package com.mossy.boundedContext.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseCode {

    // ========================================
    // 400 Bad Request (유효성 검증 및 중복 체크)
    // ========================================
    INVALID_REQUEST(400, "잘못된 요청입니다."),
    INVALID_USER_DATA(400, "전달된 사용자 정보가 유효하지 않습니다."),
    DUPLICATE_EMAIL(400, "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(400, "이미 존재하는 닉네임입니다."),
    DUPLICATE_SELLER(400, "이미 판매자로 등록되어 있습니다."),
    DUPLICATE_BUSINESS_NUMBER(400, "이미 판매자로 등록되어 있습니다."),
    DUPLICATE_SELLER_REQUEST(400, "이미 판매자 신청이 진행중입니다."),
    SELLER_REQUEST_NOT_PENDING(400, "판매자 신청이 '대기 중' 상태가 아닙니다."),
    USER_ID_REQUIRED(400, "사용자 식별자(ID)는 필수입니다."),

    // ========================================
    // 401 Unauthorized (인증 관련)
    // ========================================
    INVALID_CREDENTIALS(401, "이메일 또는 비밀번호가 일치하지 않습니다."),
    ACCOUNT_DISABLED(401, "탈퇴했거나 계정이 정지된 회원입니다."),

    // ========================================
    // 404 Not Found (리소스 없음)
    // ========================================
    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    SELLER_NOT_FOUND(404, "존재하지 않는 판매자입니다."),
    SELLER_REQUEST_NOT_FOUND(404, "판매자 신청서를 찾을 수 없습니다."),
    ROLE_NOT_FOUND(404, "권한 정보를 찾을 수 없습니다.");

    private final int status;
    private final String msg;

}