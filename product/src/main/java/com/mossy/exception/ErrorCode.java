package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseCode {

    // 1. 존재하지 않음 (404 Not Found)
    PRODUCT_NOT_FOUND(404, "해당 상품을 찾을 수 없습니다."),
    PRODUCT_ITEM_NOT_FOUND(404, "해당 상품 아이템을 찾을 수 없습니다."),
    CATALOG_PRODUCT_NOT_FOUND(404, "해당 카탈로그 상품이 존재하지 않습니다."),
    ITEM_NOT_FOUND(404, "해당 품목(Item) 정보를 찾을 수 없습니다."),
    OPTION_GROUP_NOT_FOUND(404, "해당 옵션 그룹이 존재하지 않습니다."),
    OPTION_VALUE_NOT_FOUND(404, "해당 옵션 값이 존재하지 않습니다."),

    // 2. 유효성 검증 실패 (400 Bad Request)
    INVALID_PRODUCT(400, "상품 정보가 유효하지 않습니다."),
    INVALID_PRICE(400, "상품 가격 설정이 올바르지 않습니다."),
    INVALID_ADDITIONAL_PRICE(400, "추가 금액 설정이 유효 범위를 벗어났습니다."),
    INVALID_OPTION_VALUE(400, "선택한 옵션 값이 올바르지 않습니다."),
    PRODUCT_ALREADY_APPROVED(400, "이미 승인 처리가 완료된 상품입니다."),
    PRODUCT_STATUS_INACTIVE(400, "현재 판매 중인 상태가 아닙니다."),
    PRODUCT_ITEM_INVALID_STATUS(400, "판매자가 변경할 수 없는 상품 아이템 상태입니다."),
    INVALID_STATUS_CHANGE_REQUEST(400, "판매자가 변경할 수 없는 상품 상태입니다."),
    INSUFFICIENT_STOCK(400, "상품의 재고가 부족합니다."),
    INVALID_QUANTITY_REQUEST(400, "잘못된 수량 변경 요청입니다. (0보다 커야 합니다)"),
    ALREADY_OUT_OF_STOCK(400, "이미 품절된 상품입니다."),

    // 3. 권한 및 상태 (403 Forbidden)
    PRODUCT_NOT_OWNER(403, "해당 상품의 판매자가 아닙니다. 본인이 등록한 상품만 수정할 수 있습니다."),
    UNAUTHORIZED_SELLER(403, "해당 상품에 대한 관리 권한이 없는 판매자입니다."),
    CANNOT_CHANGE_SUSPENDED_PRODUCT(403, "운영자에 의해 정지 또는 반려된 상품은 상태를 변경할 수 없습니다."),
    CANNOT_CHANGE_SUSPENDED_ITEM(403, "운영자에 의해 정지된 아이템은 상태를 변경할 수 없습니다."),

    // 4. 서버 내부 오류 및 데이터 정합성 (500 Internal Server Error)
    MIN_PRICE_CALCULATION_FAILED(500, "최저가 계산 중 서버 오류가 발생했습니다."),
    ES_INDEXING_FAILED(500, "검색 엔진 데이터 반영에 실패했습니다.")

    ;

    private final int status;
    private final String msg;
}