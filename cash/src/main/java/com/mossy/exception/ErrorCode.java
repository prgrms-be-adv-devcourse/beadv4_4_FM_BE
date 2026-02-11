package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseCode {

    // =============================================================
    // === Cash (지갑 · 예치금 · 캐시 로그) ===
    // =============================================================

    // 400 Bad Request — 금액 및 식별자 검증
    INVALID_REQUEST(400, "잘못된 요청입니다."),
    INVALID_AMOUNT(400, "잘못된 금액입니다."),
    AMOUNT_MUST_BE_POSITIVE(400, "금액은 0보다 커야 합니다."),
    USER_ID_REQUIRED(400, "사용자 식별자(ID)는 필수입니다."),
    SELLER_ID_REQUIRED(400, "판매자 식별자(ID)는 필수입니다."),
    INVALID_SELLER_DATA(400, "전달된 판매자 정보가 유효하지 않습니다."),
    INVALID_USER_DATA(400, "전달된 사용자 정보가 유효하지 않습니다."),

    // 400 Bad Request — 캐시 로그 (자금 흐름 추적용)
    REL_TYPE_CODE_IS_NULL(400, "참조 타입 코드(relTypeCode)가 없습니다."),
    REL_ID_IS_NULL(400, "참조 엔티티 ID(relId)가 없습니다."),

    // 404 Not Found — 지갑 및 사용자 조회
    USER_WALLET_NOT_FOUND(404, "구매자 지갑이 존재하지 않습니다."),
    SELLER_WALLET_NOT_FOUND(404, "판매자 지갑이 존재하지 않습니다."),
    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    SELLER_NOT_FOUND(404, "존재하지 않는 판매자입니다."),

    // 409 Conflict — 지갑 생성 충돌
    WALLET_ALREADY_EXISTS(409, "이미 생성된 지갑이 존재합니다."),

    // 422 Unprocessable Entity — 잔액 부족
    INSUFFICIENT_BALANCE(422, "잔액이 부족합니다."),
    INSUFFICIENT_WITHDRAW_BALANCE(422, "출금 가능한 잔액이 부족합니다."),

    // =============================================================
    // === Payment (결제 · 주문 · Toss 연동) ===
    // =============================================================

    // 400 Bad Request — 결제 요청 필수 값 검증
    PAYMENT_KEY_REQUIRED(400, "paymentKey는 필수입니다."),
    ORDER_ID_REQUIRED(400, "주문번호(orderId)는 필수입니다."),
    PAY_METHOD_REQUIRED(400, "결제 수단(payMethod)은 필수입니다."),
    CANCEL_AMOUNT_MUST_BE_POSITIVE(400, "취소 금액은 0보다 커야 합니다."),
    CANCEL_REASON_REQUIRED(400, "취소 사유는 필수입니다."),
    PAYMENT_DATE_IS_NULL(400, "결제 일시가 없습니다."),

    // 404 Not Found — 주문 및 결제 내역 조회
    ORDER_NOT_FOUND(404, "주문을 찾을 수 없습니다."),
    PENDING_ORDER_NOT_FOUND(404, "결제 대기 중인 주문을 찾을 수 없습니다."),
    PAID_ORDER_NOT_FOUND(404, "결제 완료 상태의 주문을 찾을 수 없습니다."),
    PAID_PAYMENT_NOT_FOUND(404, "결제 완료된 내역를 찾을 수 없습니다."),

    // 409 Conflict — 결제 검증 위반
    ORDER_AMOUNT_MISMATCH(409, "주문 금액이 일치하지 않습니다."),
    ORDER_ALREADY_PAID(409, "이미 결제가 완료된 주문입니다."),

    // 502 Bad Gateway — 외부 서비스 오류 (Toss)
    TOSS_PAYMENT_CONFIRM_FAILED(502, "토스페이먼츠 결제 승인에 실패했습니다."),
    TOSS_PAYMENT_CANCEL_FAILED(502, "토스페이먼츠 결제 취소에 실패했습니다."),
    TOSS_PAYMENT_NOT_FOUND(502, "토스페이먼츠에서 결제 내역을 찾을 수 없습니다."),
    TOSS_API_ERROR(502, "토스페이먼츠 API 통신 중 오류가 발생했습니다.");

    private final int status;
    private final String msg;
}
