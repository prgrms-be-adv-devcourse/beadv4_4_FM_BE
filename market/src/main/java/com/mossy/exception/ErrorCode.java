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
    INVALID_COUPON_PERIOD(400, "쿠폰 시작일은 종료일보다 이전이어야 합니다."),
    INVALID_COUPON_START_AT(400, "쿠폰 시작일은 현재 시간 이후여야 합니다."),
    INVALID_DISCOUNT_VALUE(400, "정률 할인은 0~100 사이의 값이어야 합니다."),

    // ========================================
    // 403 Forbidden (접근 거부)
    // ========================================
    ORDER_ACCESS_DENIED(403, "주문에 접근할 수 없습니다."),
    COUPON_ACCESS_DENIED(403, "해당 쿠폰에 접근할 수 없습니다."),

    // ========================================
    // 404 Not Found (리소스 없음)
    // ========================================
    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    COUPON_NOT_FOUND(404, "쿠폰이 존재하지 않습니다."),
    USER_COUPON_NOT_FOUND(404, "보유한 쿠폰이 존재하지 않습니다."),
    CART_NOT_FOUND(404, "장바구니가 존재하지 않습니다."),
    CART_ITEM_NOT_FOUND(404, "장바구니에 해당 상품이 없습니다."),
    ORDER_NOT_FOUND(404, "주문이 존재하지 않습니다."),

    // ========================================
    // 409 Conflict (충돌 / 상태 위반)
    // ========================================
    COUPON_ALREADY_USED(409, "이미 사용된 쿠폰입니다."),
    COUPON_ALREADY_DOWNLOADED(409, "이미 다운로드한 쿠폰입니다."),
    COUPON_DOWNLOAD_LOCKED(409, "쿠폰 다운로드가 진행 중입니다. 잠시 후 다시 시도해주세요."),
    COUPON_DOWNLOAD_FAILED(500, "쿠폰 다운로드 중 오류가 발생했습니다."),
    COUPON_EXPIRED(409, "만료된 쿠폰입니다."),
    ORDER_AMOUNT_MISMATCH(409, "주문 금액이 일치하지 않습니다."),
    INVALID_ORDER_STATE(409, "유효하지 않은 주문 상태입니다."),
    ORDER_ALREADY_PAID(409, "이미 결제가 완료된 주문입니다."),
    DUPLICATE_ORDER_REQUEST(409, "중복된 주문 요청입니다."),
    ORDER_CREATION_FAILED(409, "주문 생성 중 오류가 발생했습니다."),

    // ========================================
    // 422 Unprocessable Entity (비즈니스 규칙 위배)
    // ========================================
    QUANTITY_LIMIT_EXCEEDED(422, "수량 제한을 초과했습니다."),
    COUPON_NOT_DELETABLE(422, "활성 중인 쿠폰은 삭제할 수 없습니다. 비활성화하거나 기간이 만료된 후 삭제해주세요."),
    ORDER_CANNOT_DELETE(422, "삭제할 수 없는 주문입니다."),
    ORDER_CANNOT_CANCEL(422, "취소할 수 없는 주문입니다."),
    ORDER_PURCHASE_CONFIRMED(422, "구매 확정된 주문은 취소할 수 없습니다.");

    private final int status;
    private final String msg;
}