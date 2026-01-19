package backend.mossy.global.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final String resultCode;
    private final String msg;
    private final ErrorCode errorCode;

    public DomainException(String resultCode, String msg) {
        super(resultCode + " : " + msg);
        this.resultCode = resultCode;
        this.msg = msg;
        this.errorCode = null;
    }

    //회원이 사용하는 생성자
    public DomainException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.resultCode = String.valueOf(errorCode.getStatus());
        this.msg = errorCode.getMsg();
        this.errorCode = errorCode;
    }
}