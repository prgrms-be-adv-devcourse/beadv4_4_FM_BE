package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements BaseCode {
    PAYOUT_DAILY_WALLET_NOTHING_TO_PROCESS(200, "처리할 정산이 없습니다."),
    PAYOUT_DAILY_WALLET_CREDIT_PROCESSED(201, "판매자 지급 처리가 완료되었습니다."),
    DONATION_LOGS_FOUND(200, "기부 내역 조회에 성공했습니다."),
    PAYOUT_LIST_FOUND(200, "정산 목록 조회에 성공했습니다.");

    private final int status;
    private final String msg;
}
