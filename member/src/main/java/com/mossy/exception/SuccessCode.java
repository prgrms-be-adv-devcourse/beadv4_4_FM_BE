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
    CHANGE_PASSWORD_COMPLETE(200, "비밀번호가 변경되었습니다."),
    CHANGE_ADDRESS_COMPLETE(200, "주소가 변경되었습니다."),
    CHANGE_PHONE_COMPLETE(200, "전화번호가 변경되었습니다."),
    CHANGE_NICKNAME_COMPLETE(200, "닉네임이 변경되었습니다."),
    SET_PASSWORD_COMPLETE(200, "비밀번호가 설정되었습니다."),
    CHANGE_PROFILE_IMAGE_COMPLETE(200, "프로필 이미지가 변경되었습니다."),
    DELETE_PROFILE_IMAGE_COMPLETE(200, "프로필 이미지가 삭제되었습니다."),

    // 판매자
    SELLER_REQUEST_COMPLETE(200, "판매자 신청이 완료되었습니다."),

    // 관리자
    SELLER_APPROVE_COMPLETE(200, "판매자 승인이 완료되었습니다."),
    SELLER_REJECT_COMPLETE(200, "판매자 반려가 완료되었습니다."),
    GET_SELLER_REQUESTS_COMPLETE(200, "판매자 신청 목록 조회가 완료되었습니다.");

    private final int status;
    private final String msg;
}