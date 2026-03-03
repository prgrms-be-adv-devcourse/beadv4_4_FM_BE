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


    // 지갑 및 잔액 변경 관련 검증


    // 결제 관련 검증


    // 정산/기부 관련 입력 검증


    // ========================================
    // 401 Unauthorized (인증 실패)
    // ========================================


    // ========================================
    // 403 Forbidden (접근 거부)
    // ========================================


    // ========================================
    // 404 Not Found (리소스 없음)
    // ========================================
    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    REVIEWABLE_ITEM_NOT_FOUND(404, "리뷰 가능한 주문 항목을 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(404, "리뷰를 찾을 수 없습니다."),
    ALREADY_REVIEWED(409, "이미 리뷰를 작성한 주문 항목입니다."),
    REVIEW_UNAUTHORIZED(403, "본인의 주문 항목에만 리뷰를 작성할 수 있습니다.");

    // ========================================
    // 409 Conflict (충돌 / 상태 위반)
    // ========================================


    // ========================================
    // 422 Unprocessable Entity (비즈니스 규칙 위배)
    // ========================================

    // ========================================
    // 502 Bad Gateway (외부 서비스 오류)
    // ========================================


    private final int status;
    private final String msg;
}
