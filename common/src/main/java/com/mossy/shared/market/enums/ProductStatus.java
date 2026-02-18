package com.mossy.shared.market.enums;

import lombok.Getter;

public enum ProductStatus {
    // 정상 프로세스
    UNDER_REVIEW("검수 중"),
    FOR_SALE("판매 중"),
    PRE_ORDER("예약 판매"),

    // 일시적 중단
    OUT_OF_STOCK("품절"),

    // 영구적/강제적 중단
    DISCONTINUED("판매 중단"),    // 판매자
    SUSPENDED("판매 정지"),      // 운영자의 강제 조치
    REJECTED("거절/반려"),       // 검수 탈락

    // 관리용
    HIDDEN("비공개"),
    DELETED("삭제")
    ;

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }
}
