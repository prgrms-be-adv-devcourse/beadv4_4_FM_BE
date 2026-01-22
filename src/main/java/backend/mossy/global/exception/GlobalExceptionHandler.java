package backend.mossy.global.exception;

import backend.mossy.global.rsData.RsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<RsData<Void>> handleDomainException(DomainException e) {

        // RsData 전환 과정에서 기존 DomainException 사용 코드도 함께 지원하기 위한 처리
        // TODO: RsData 전환 완료 후 DomainException 단일 방식으로 정리 예정


        if (e.getErrorCode() != null) {
            ErrorCode errorCode = e.getErrorCode();

            return ResponseEntity
                    .status(errorCode.getStatus())
                    .body(RsData.fail(errorCode));
        }

        int status = 400;
        try {
            status = Integer.parseInt(e.getResultCode().replace("F-", ""));
        } catch (Exception ignored) {}

        return ResponseEntity
                .status(status)
                .body(
                        RsData.fail(
                                e.getResultCode(),
                                e.getMsg()
                        )
                );
    }
}
