package com.mossy.shared.market.enums;

public enum ProductItemStatus {
    ON_SALE("판매 중"),
    STOPPED("판매 중지"),
    HIDDEN("숨김"),
    DELETED("삭제");

    private final String description;
    ProductItemStatus(String description) {
        this.description = description;
    }
}
