package com.mossy.shared.market.enums;

public enum CatalogStatus {
    PREPARE("검토 중"),
    ACTIVE("판매 중"),
    SUSPENDED("판매 중지"),
    DISCONTINUED("판매 종료")
    ;
    private final String description;

    CatalogStatus(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}


