package backend.mossy.global.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final ErrorCode errorCode;

    //TODO:윤호님 피드백 반영 나중에 얘기 나눠요

    public DomainException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }
}