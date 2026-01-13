package backend.mossy.global.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final String resultCode;
    private final String msg;

    public DomainException(String resultCode, String msg) {
        super(resultCode + " : " + msg);
        this.resultCode = resultCode;
        this.msg = msg;
    }
}