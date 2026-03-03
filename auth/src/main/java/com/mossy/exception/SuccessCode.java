package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode {

    LOGIN_COMPLETE(200, "로그인이 완료되었습니다."),
    REISSUE_COMPLETE(200, "토큰 재발급이 완료되었습니다."),
    LOGOUT_COMPLETE(200, "로그아웃이 완료되었습니다."),
    SIGNUP_COMPLETE(201, "회원가입이 완료되었습니다."),

    OK(200, "성공");

    private final int status;
    private final String msg;
}