package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseCode {

    // Cart
    CART_GET(200, "장바구니 조회를 성공했습니다."),
    CART_ITEM_ADD(200, "상품이 장바구니에 추가되었습니다."),
    CART_ITEM_UPDATE(200, "장바구니 상품 수량이 수정되었습니다."),
    CART_ITEM_REMOVE(200, "상품이 삭제되었습니다."),
    CART_CLEAR(200, "장바구니가 비워졌습니다."),

    // Coupon
    COUPON_CREATE(200, "쿠폰이 생성되었습니다."),
    COUPON_LIST(200, "다운로드 가능한 쿠폰 목록 조회를 성공했습니다."),
    MY_COUPON_LIST(200, "내 쿠폰 목록 조회를 성공했습니다."),
    APPLICABLE_COUPON_LIST(200, "적용 가능한 쿠폰 목록 조회를 성공했습니다."),
    COUPON_DOWNLOAD(200, "쿠폰이 다운로드되었습니다."),
    COUPON_UPDATE(200, "쿠폰이 수정되었습니다."),
    COUPON_DEACTIVATE(200, "쿠폰이 비활성화되었습니다."),

    // Order
    ORDER_CREATE(200, "주문이 생성되었습니다."),
    ORDER_LIST(200, "구매 내역 목록 조회를 성공했습니다."),
    ORDER_DETAIL(200, "구매 내역 상세 조회를 성공했습니다."),
    ORDER_DELETE(200, "주문 삭제를 성공했습니다."),
    ORDER_CANCEL(200, "주문이 취소되었습니다."),

    // Seller Order
    SELLER_ORDER_LIST(200, "판매자 판매 내역 목록 조회를 성공했습니다."),
    SELLER_ORDER_DETAIL(200, "판매자 판매 내역 상세 조회를 성공했습니다."),

    // Wishlist
    WISHLIST_ADD(200, "찜 완료."),
    WISHLIST_DELETE(200, "찜 삭제 완료."),
    WISHLIST_GET(200, "찜 목록 조회 성공."),
    WISHLIST_CHECK(200, "찜 여부 확인 완료.");

    private final int status;
    private final String msg;
}
