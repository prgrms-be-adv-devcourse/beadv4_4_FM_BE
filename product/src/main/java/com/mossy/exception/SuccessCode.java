package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseCode {

    GET_CATALOG_SUCCESS(200, "카탈로그 정보 조회가 완료되었습니다."),
    GET_PRODUCT_SUCCESS(200, "상품 상세 정보 조회가 완료되었습니다."),
    CREATE_PRODUCT_SUCCESS(201, "상품이 등록되었습니다."),
    UPDATE_PRODUCT_SUCCESS(200, "상품이 수정되었습니다."),
    UPDATE_PRODUCT_STATUS_SUCCESS(200, "상품 상태가 수정되었습니다."),
    UPDATE_PRODUCT_ITEM_STATUS_SUCCESS(200, "상품 아이템 상태가 수정되었습니다."),


    DELETE_PRODUCT_SUCCESS(200, "상품이 삭제되었습니다."),
    DELETE_PRODUCT_ITEM_SUCCESS(200, "상품 아이템이 삭제되었습니다."),

    ;

    private final int status;
    private final String msg;
}
