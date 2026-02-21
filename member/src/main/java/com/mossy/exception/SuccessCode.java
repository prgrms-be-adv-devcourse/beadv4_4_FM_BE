package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode {

    //유저
    SIGNUP_COMPLETE(201, "회원가입이 완료되었습니다."),
    GET_MY_INFO_COMPLETE(200, "내 정보 조회가 완료되었습니다."),

    // 판매자
    SELLER_REQUEST_COMPLETE(200, "판매자 신청이 완료되었습니다."),

    // 관리자
    SELLER_APPROVE_COMPLETE(200, "판매자 승인이 완료되었습니다."),
    SELLER_REJECT_COMPLETE(200, "판매자 반려가 완료되었습니다."),
    GET_SELLER_REQUESTS_COMPLETE(200, "판매자 신청 목록 조회가 완료되었습니다.");

    private final int status;
    private final String msg;
}