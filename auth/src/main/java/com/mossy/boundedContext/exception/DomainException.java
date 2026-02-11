package com.mossy.boundedContext.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final String resultCode;
    private final String msg;
    private final Code errorCode;

    public DomainException(String resultCode, String msg) {
        super(resultCode + ":" + msg);
        this.resultCode = resultCode;
        this.msg = msg;
        this.errorCode = null;
    }

    public DomainException(Code errorCode) {
        super(errorCode.getMsg());
        this.resultCode = "F-" + errorCode.getStatus();
        this.msg = errorCode.getMsg();
        this.errorCode = errorCode;

    }

    public int getHttpStatus() {
        if (errorCode != null) {
            return errorCode.getStatus();
        }
        try {
            return Integer.parseInt(resultCode.replace("F-", ""));
        } catch (Exception e) {
            return 400;
        }


    }

}