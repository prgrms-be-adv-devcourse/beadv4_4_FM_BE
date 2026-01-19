package backend.mossy.boundedContext.market.domain;

public enum ProductStatus {
    FOR_SALE("판매 중"),
    OUT_OF_STOCK("품절"),
    DISCONTINUED("판매 중단"),
    PRE_ORDER("예약 판매"),
    HIDDEN("비공개"),
    DELETED("삭제")
    ;

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
