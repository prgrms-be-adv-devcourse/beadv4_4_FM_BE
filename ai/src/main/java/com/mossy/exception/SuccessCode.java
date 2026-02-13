package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseCode {

    // =============================================================
    // === Recommendation
    // =============================================================
    RECOMMENDATION_FOUND(200, "추천 상품 목록을 조회했습니다."),
    ;

    private final int status;
    private final String msg;
}