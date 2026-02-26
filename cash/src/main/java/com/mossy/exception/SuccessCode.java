package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseCode {

    // =============================================================
    // === Cash (지갑 · 예치금 · 캐시 로그) ===
    // =============================================================
    CASH_CREDIT_SUCCESS(200, "예치금이 성공적으로 충전되었습니다."),
    CASH_DEDUCT_SUCCESS(200, "예치금 차감이 완료되었습니다."),
    USER_WALLET_FOUND(200, "구매자 지갑 정보가 정상적으로 조회되었습니다."),
    USER_BALANCE_FOUND(200, "구매자 잔액 조회 성공"),
    USER_CASH_LOGS_FOUND(200, "구매자 캐시 내역 조회 성공"),
    SELLER_CREDIT_SUCCESS(200, "판매 대금 입금이 완료되었습니다."),
    SELLER_DEDUCT_SUCCESS(200, "정산용 잔액 차감이 완료되었습니다."),
    SELLER_WALLET_FOUND(200, "판매자 지갑 정보가 정상적으로 조회되었습니다."),
    SELLER_BALANCE_FOUND(200, "판매자 잔액 조회 성공"),
    SELLER_CASH_LOGS_FOUND(200, "판매자 캐시 내역 조회 성공"),

    // =============================================================
    // === Payment (결제 · 주문 · Toss 연동) ===
    // =============================================================
    TOSS_PAYMENT_CONFIRMED(200, "결제가 완료되었습니다."),
    CASH_PAYMENT_CONFIRMED(200, "예치금 결제가 완료되었습니다."),
    PAYMENT_CANCELLED(200, "결제가 취소되었습니다."),
    TOSS_PAYMENT_CANCELLED(200, "PG-결제가 취소되었습니다."),
    CASH_PAYMENT_CANCELLED(200, "예치금 결제가 취소되었습니다."),
    PAYMENT_HISTORY_FOUND(200, "주문 결제 이력 조회 성공"),
    TOSS_PAYMENT_INFO_FOUND(200, "토스 결제 원본 정보 조회 성공");

    private final int status;
    private final String msg;
}