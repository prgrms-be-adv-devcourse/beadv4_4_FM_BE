package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseCode {

    // =============================================================
    // === Recommendation (추천 아이템 · 임베딩 · AI)
    // =============================================================

    // 400 Bad Request
    INVALID_RECOMMENDATION_REQUEST(400, "잘못된 추천 요청입니다."),
    CONTENT_REQUIRED(400, "임베딩 대상 콘텐츠가 비어 있습니다."),

    // 404 Not Found
    ITEM_NOT_FOUND(404, "추천 아이템을 찾을 수 없습니다."),

    // 409 Conflict
    ITEM_ALREADY_EXISTS(409, "이미 등록된 상품입니다."),

    // 502 Bad Gateway
    EMBEDDING_FAILED(502, "임베딩 생성 중 오류가 발생했습니다."),
    AI_GENERATION_FAILED(502, "AI 추천 사유 생성에 실패했습니다."),
    FEIGN_CALL_FAILED(502, "상품 서비스 호출에 실패했습니다."),
    ;

    private final int status;
    private final String msg;
}
