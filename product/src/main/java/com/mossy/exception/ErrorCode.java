package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseCode {

    PRODUCT_NOT_FOUND(404, "해당 상품이 존재하지 않습니다."),
    CATALOG_PRODUCT_NOT_FOUND(404, "해당 카탈로그 상품이 존재하지 않습니다."),
    INVALID_PRODUCT(400, "유효하지 않은 상품입니다."),

    // 상태 관련 (승인/판매 상태)
    PRODUCT_NOT_APPROVED(403, "승인되지 않은 상품입니다. 관리자의 승인이 필요합니다."),
    PRODUCT_ALREADY_APPROVED(400, "이미 승인된 상품입니다."),
    PRODUCT_STATUS_INACTIVE(400, "판매 중인 상품이 아닙니다."),

    // 권한 관련
    UNAUTHORIZED_SELLER(403, "해당 상품에 대한 관리 권한이 없는 판매자입니다."),

    // 3. 데이터 정합성 관련
    MIN_PRICE_CALCULATION_FAILED(500, "최저가 계산 중 오류가 발생했습니다."),
    ES_INDEXING_FAILED(500, "검색 엔진 데이터 저장에 실패했습니다.");

    ;

    private final int status;
    private final String msg;
}