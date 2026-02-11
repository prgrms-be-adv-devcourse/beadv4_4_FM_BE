package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseCode {

    // ========================================
    // 400 Bad Request (잘못된 요청 / 유효성 검증 실패)
    // ========================================
    INVALID_REQUEST(400, "잘못된 요청입니다."),
    INVALID_AMOUNT(400, "잘못된 금액입니다."),
    DUPLICATE_EMAIL(400, "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(400, "이미 존재하는 닉네임입니다."),
    DUPLICATE_SELLER_REQUEST(400, "이미 판매자 신청이 진행중입니다."),
    DUPLICATE_BUSINESS_NUMBER(400, "이미 판매자로 등록되어 있습니다."),
    DUPLICATE_SELLER(400, "이미 판매자로 등록되어 있습니다."),
    SELLER_REQUEST_NOT_PENDING(400, "판매자 신청이 '대기 중' 상태가 아닙니다.")
    ;

    private final int status;
    private final String msg;
}