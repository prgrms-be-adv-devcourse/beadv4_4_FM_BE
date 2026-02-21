package com.mossy.shared.product.enums;

public enum ProductStatus {

    UNDER_REVIEW("검수 중"),

    // 화면 노출
    FOR_SALE("판매 중"),
    PRE_ORDER("예약 판매"),
    OUT_OF_STOCK("품절"),

    // 화면 노출 X
    DISCONTINUED("판매 중단"),    // 판매자
    SUSPENDED("판매 정지"),      // 운영자의 강제 조치
    REJECTED("거절/반려"),       // 검수 탈락

    HIDDEN("비공개"),
    DELETED("삭제")
    ;

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }
}
