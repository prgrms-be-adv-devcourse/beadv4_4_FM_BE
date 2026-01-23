package backend.mossy.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //400 Bad Request
    DUPLICATE_EMAIL(400, "이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME(400, "이미 존재하는 닉네임입니다."),
    REL_TYPE_CODE_IS_NULL(400,"참조 타입 코드가 없습니다."),
    REL_ID_IS_NULL(400,"참조 엔티티 ID가 없습니다."),
    PAYEE_IS_NULL(400,"수취인이 없습니다."),
    INVALID_AMOUNT(400,"금액이 올바르지 않습니다."),
    INVALID_PAYOUT_AMOUNT(400,"정산 금액은 0원보다 작을 수 없습니다"),
    INVALID_BATCH_LIMIT(400,"배치 처리 한계값이 올바르지 않습니다."),
    INVALID_DONATION_AMOUNT(400,"기부 금액이 올바르지 않습니다."),
    INVALID_CARBON_AMOUNT(400,"탄소 배출량이 올바르지 않습니다."),
    INVALID_CARBON_CALCULATION_INPUT(400, "탄소 배출량 계산을 위한 입력값이 올바르지 않습니다."),
    INVALID_DONATION_CALCULATION_INPUT(400, "기부금 계산을 위한 주문 정보가 올바르지 않습니다."),
    INVALID_PAYOUT_FEE(400,"정산 수수료 금액이 올바르지 않습니다."),
    INVALID_PAYEE_ID(400, "수취인 식별자(ID)가 올바르지 않습니다."),
    INVALID_SELLER_DATA(400, "전달된 판매자 정보가 유효하지 않습니다."),
    INVALID_USER_DATA(400, "전달된 사용자 정보가 유효하지 않습니다."),
    PAYMENT_DATE_IS_NULL(400,"결제 일시가 없습니다."),
    PAYOUT_IS_NULL(400,"정산 객체가 없습니다."),
    PAYOUT_EVENT_TYPE_IS_NULL(400,"정산 이벤트 타입이 없습니다."),
    // status: 400 BAD_REQUEST





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
    BUYER_NOT_FOUND(404, "존재하지 않는 구매자입니다."),
    SELLER_NOT_FOUND(404, "존재하지 않는 판매자입니다."),
    SYSTEM_SELLER_NOT_FOUND(404, "시스템 판매자를 찾을 수 없습니다."),

    CART_NOT_FOUND(404, "장바구니가 존재하지 않습니다."),
    CART_ITEM_NOT_FOUND(404, "장바구니에 해당 상품이 없습니다."),

    PRODUCT_NOT_FOUND(404, "해당 상품이 존재하지 않습니다."),
    WEIGHT_GRADE_NOT_FOUND(404, "무게 등급이 존재하지 않습니다."),
    DELIVERY_DISTANCE_NOT_FOUND(404, "배송 거리 등급이 존재하지 않습니다."),

    ORDER_NOT_FOUND(404, "주문이 존재하지 않습니다."),

    ORDERITEM_IS_NULL(404, "주문 목록이 없습니다."),

    PAYOUT_NOT_FOUND(404, "해당 판매자의 활성 정산을 찾을 수 없습니다."),
    PAYOUT_USER_NOT_FOUND(404,"기부자를 찾을 수 없습니다."),
    PAYOUT_SELLER_NOT_FOUND(404, "정산 대상 판매자를 찾을 수 없습니다."),

    DONATION_SELLER_NOT_FOUND(404,"기부금 수령 판매자가 존재하지 않습니다."),
    DONATION_PAYOUT_ITEM_NOT_FOUND(404,"정산 대상 기부 항목이 존재하지 않습니다."),
    DONATION_LOG_NOT_FOUND(404,"기부 로그가 존재하지 않습니다."),




    //422 Unprocessable Entity (비즈니스 규칙 위배)
    QUANTITY_LIMIT_EXCEEDED(422, "수량 제한을 초과했습니다."),
    ORDER_CANNOT_DELETE(422, "삭제할 수 없는 주문입니다."),
    ALREADY_SETTLED_DONATION(422, "이미 정산 완료된 기부 내역입니다."),
    ALREADY_COMPLETED_PAYOUT(422,"이미 완료된 정산건입니다.");
    private final int status;
    private final String msg;
}
