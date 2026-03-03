package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;

@Getter
public enum SuccessCode implements BaseCode {
    REVIEW_CREATED(201, "리뷰가 작성되었습니다."),
    REVIEW_GET(200, "리뷰가 조회되었습니다."),
    REVIEW_LIST_GET(200, "리뷰 목록이 조회되었습니다."),
    REVIEW_PENDING_LIST_GET(200, "작성 가능한 리뷰 목록이 조회되었습니다."),
    REVIEW_MY_LIST_GET(200, "내가 작성한 리뷰 목록이 조회되었습니다.");

    private final int status;
    private final String msg;

    SuccessCode(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() { return status; }
    public String getMsg() { return msg; }
}
