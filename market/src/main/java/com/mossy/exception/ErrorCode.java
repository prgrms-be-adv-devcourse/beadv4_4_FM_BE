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
    SELLER_REQUEST_NOT_PENDING(400, "판매자 신청이 '대기 중' 상태가 아닙니다."),

    // ========================================
    // 401 Unauthorized (인증 실패)
    // ========================================
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "만료된 토큰입니다."),
    TOKEN_SIGNATURE_ERROR(401, "토큰 서명이 일치하지 않습니다."),
    INVALID_CREDENTIALS(401, "이메일 또는 비밀번호가 일치하지 않습니다."),
    ACCOUNT_DISABLED(401, "탈퇴했거나 계정이 정지된 회원입니다."),

    // ========================================
    // 403 Forbidden (접근 거부)
    // ========================================
    ORDER_ACCESS_DENIED(403, "주문에 접근할 수 없습니다."),
    COUPON_ACCESS_DENIED(403, "해당 쿠폰에 접근할 수 없습니다."),

    // ========================================
    // 404 Not Found (리소스 없음)
    // ========================================
    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    BUYER_NOT_FOUND(404, "존재하지 않는 구매자입니다."),
    SELLER_NOT_FOUND(404, "존재하지 않는 판매자입니다."),
    SYSTEM_SELLER_NOT_FOUND(404, "시스템 판매자를 찾을 수 없습니다."),

    COUPON_NOT_FOUND(404, "쿠폰이 존재하지 않습니다."),
    USER_COUPON_NOT_FOUND(404, "보유한 쿠폰이 존재하지 않습니다."),

    CART_NOT_FOUND(404, "장바구니가 존재하지 않습니다."),
    CART_ITEM_NOT_FOUND(404, "장바구니에 해당 상품이 없습니다."),
    PRODUCT_NOT_FOUND(404, "해당 상품이 존재하지 않습니다."),

    ORDER_NOT_FOUND(404, "주문이 존재하지 않습니다."),
    PENDING_ORDER_NOT_FOUND(404, "결제 대기 중인 주문을 찾을 수 없습니다."),
    PAID_PAYMENT_NOT_FOUND(404, "결제 완료된 내역를 찾을 수 없습니다."),
    ORDER_ITEM_IS_NULL(404, "주문 목록이 없습니다."),
    SELLER_REQUEST_NOT_FOUND(404, "판매자 신청서를 찾을 수 없습니다."),
    ROLE_NOT_FOUND(404, "권한 정보를 찾을 수 없습니다."),

    // ========================================
    // 409 Conflict (충돌 / 상태 위반)
    // ========================================
    COUPON_ALREADY_USED(409, "이미 사용된 쿠폰입니다."),
    COUPON_ALREADY_DOWNLOADED(409, "이미 다운로드한 쿠폰입니다."),
    COUPON_EXPIRED(409, "만료된 쿠폰입니다."),
    INVALID_COUPON_PERIOD(400, "쿠폰 시작일은 종료일보다 이전이어야 합니다."),
    INVALID_COUPON_START_AT(400, "쿠폰 시작일은 현재 시간 이후여야 합니다."),

    ORDER_AMOUNT_MISMATCH(409, "주문 금액이 일치하지 않습니다."),
    INVALID_ORDER_STATE(409, "유효하지 않은 주문 상태입니다."),
    ORDER_ALREADY_PAID(409, "이미 결제가 완료된 주문입니다."),
    WALLET_ALREADY_EXISTS(409, "이미 생성된 지갑이 존재합니다."),
    DUPLICATE_ORDER_REQUEST(409, "중복된 주문 요청입니다."),
    ORDER_CREATION_FAILED(409, "주문 생성 중 오류가 발생했습니다."),

    // ========================================
    // 422 Unprocessable Entity (비즈니스 규칙 위배)
    // ========================================
    QUANTITY_LIMIT_EXCEEDED(422, "수량 제한을 초과했습니다."),
    ORDER_CANNOT_DELETE(422, "삭제할 수 없는 주문입니다."),
    ORDER_CANNOT_CANCEL(422, "취소할 수 없는 주문입니다."),
    ORDER_PURCHASE_CONFIRMED(422, "구매 확정된 주문은 취소할 수 없습니다."),
    INSUFFICIENT_BALANCE(422, "잔액이 부족합니다."),
    INSUFFICIENT_WITHDRAW_BALANCE(422, "출금 가능한 잔액이 부족합니다."),
    INSUFFICIENT_STOCK(422, "재고가 부족합니다."),
    INVALID_DEDUCT_QUANTITY(422, "차감할 수량은 0보다 커야 합니다."),
    ALREADY_SETTLED_DONATION(422, "이미 정산 완료된 기부 내역입니다."),
    ALREADY_COMPLETED_PAYOUT(422, "이미 완료된 정산건입니다.");

    private final int status;
    private final String msg;
}