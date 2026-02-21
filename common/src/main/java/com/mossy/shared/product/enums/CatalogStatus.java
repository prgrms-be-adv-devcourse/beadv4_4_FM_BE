package com.mossy.shared.product.enums;

public enum CatalogStatus {
    DRAFT("초안"),               // 등록 중 (임시저장 상태, 노출되지 않음)
    ACTIVE("활성"),              // 정상 노출 및 판매자 상품 등록 가능
    INACTIVE("비활성"),          // 정보 수정 등을 위해 잠시 노출 제외
    SUSPENDED("판매 정지"),       // 정책 위반이나 문제 발생 시 운영자가 강제 중지
    DISCONTINUED("판매 종료/단종") // 더 이상 생산/판매되지 않는 상품 (조회는 가능하나 신규 등록 불가)
    ;

    private final String description;
    CatalogStatus(String description) { this.description = description; }
}


