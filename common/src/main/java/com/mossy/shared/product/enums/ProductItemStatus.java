package com.mossy.shared.product.enums;

public enum ProductItemStatus {

    UNDER_REVIEW("검수 중"),

    // 화면 노출
    PRE_ORDER("예약 판매"),
    ON_SALE("판매 중"),
    OUT_OF_STOCK("품절"),

    // 화면 노출 X
    STOPPED("판매 중지"),       // 판매자

    SUSPENDED("판매 정지"),     // 관리자
    REJECTED("거절/반려"),      // 관리자

    HIDDEN("숨김"),
    DELETED("삭제")
    ;

    private final String description;
    ProductItemStatus(String description) { this.description = description; }
}
