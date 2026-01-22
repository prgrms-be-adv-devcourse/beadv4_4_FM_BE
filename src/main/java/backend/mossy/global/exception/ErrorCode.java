package backend.mossy.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //400 Bad Request
    DUPLICATE_EMAIL(400, "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(400, "이미 존재하는 닉네임입니다."),

    //401 Unauthorized (토큰 관련)
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "만료된 토큰입니다."),
    TOKEN_SIGNATURE_ERROR(401, "토큰 서명이 일치하지 않습니다."),
    INVALID_CREDENTIALS(401, "이메일 또는 비밀번호가 일치하지 않습니다."),
    ACCOUNT_DISABLED(401, "탈퇴했거나 계정이 정지된 회원입니다."),

    //403 Forbidden
    ORDER_ACCESS_DENIED(403, "주문에 접근할 수 없습니다."),

    //404 NOT FOUND
    USER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    SELLER_NOT_FOUND(404, "존재하지 않는 판매자입니다."),
    CART_NOT_FOUND(404, "장바구니가 존재하지 않습니다."),
    CART_ITEM_NOT_FOUND(404, "장바구니에 해당 상품이 없습니다."),
    PRODUCT_NOT_FOUND(404, "해당 상품이 존재하지 않습니다."),
    WEIGHT_GRADE_NOT_FOUND(404, "무게 등급이 존재하지 않습니다."),
    DELIVERY_DISTANCE_NOT_FOUND(404, "배송 거리 등급이 존재하지 않습니다."),
    ORDER_NOT_FOUND(404, "주문이 존재하지 않습니다."),
    PENDING_ORDER_NOT_FOUND(404, "결제 대기 중인 주문을 찾을 수 없습니다."),

    //409 Conflict
    ORDER_AMOUNT_MISMATCH(409, "주문 금액이 일치하지 않습니다."),
    INVALID_ORDER_STATE(409, "유효하지 않은 주문 상태입니다."),

    //422 Unprocessable Entity (비즈니스 규칙 위배)
    QUANTITY_LIMIT_EXCEEDED(422, "수량 제한을 초과했습니다."),
    ORDER_CANNOT_DELETE(422, "삭제할 수 없는 주문입니다."),

    //502 Bad Gateway (외부 서비스 오류)
    TOSS_PAYMENT_CONFIRM_FAILED(502, "토스페이먼츠 결제 승인에 실패했습니다."),
    TOSS_PAYMENT_CANCEL_FAILED(502, "토스페이먼츠 결제 취소에 실패했습니다.");

    private final int status;
    private final String msg;
}
