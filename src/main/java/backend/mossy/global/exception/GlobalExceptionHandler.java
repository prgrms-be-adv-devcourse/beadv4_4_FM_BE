package backend.mossy.global.exception;

import backend.mossy.global.rsData.RsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    //DomainException 처리 (ErrorCode 기반)
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<RsData<Void>> handleDomainException(DomainException e) {
        log.warn("DomainException: {}", e.getMessage());

        if (e.getErrorCode() != null) {
            ErrorCode errorCode = e.getErrorCode();
            return ResponseEntity
                    .status(errorCode.getStatus())
                    .body(RsData.fail(errorCode));
        }

        int status = e.getHttpStatus();
        return ResponseEntity
                .status(status)
                .body(RsData.fail(e.getResultCode(), e.getMsg()));
    }

    //IllegalArgumentException 처리 (DTO 유효성 검증 실패 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RsData<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(RsData.fail("F-400", e.getMessage()));
    }


     //IllegalStateException 처리 (비즈니스 로직 상태 오류)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<RsData<Void>> handleIllegalStateException(IllegalStateException e) {
        log.warn("IllegalStateException: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(RsData.fail("F-409", e.getMessage()));
    }

    //@Valid 유효성 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("유효성 검증에 실패했습니다.");

        log.warn("ValidationException: {}", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(RsData.fail("F-400", message));
    }


    //예상하지 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RsData<Void>> handleException(Exception e) {
        log.error("Unexpected Exception: ", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RsData.fail("F-500", "서버 오류가 발생했습니다."));
    }
}
