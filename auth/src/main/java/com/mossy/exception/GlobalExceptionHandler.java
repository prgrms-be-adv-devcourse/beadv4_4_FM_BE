package com.mossy.exception;

import com.mossy.global.rsData.RsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import feign.FeignException;

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
                .body(new RsData<>(e.getResultCode(), e.getMsg(), null));
    }

    //IllegalArgumentException 처리 (DTO 유효성 검증 실패 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RsData<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RsData<>("F-400", e.getMessage(), null));
    }


    //IllegalStateException 처리 (비즈니스 로직 상태 오류)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<RsData<Void>> handleIllegalStateException(IllegalStateException e) {
        log.warn("IllegalStateException: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new RsData<>("F-409", e.getMessage(), null));
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
                .body(new RsData<>("F-400", message, null));
    }


    // FeignException 처리 (회원 서비스 등 외부 API 호출 실패)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<RsData<Void>> handleFeignException(FeignException e) {
        log.warn("FeignException: {} - {}", e.status(), e.getMessage());

        if (e.status() == 404 || e.status() == 401 || e.status() == 400) {
            return ResponseEntity
                    .status(ErrorCode.INVALID_CREDENTIALS.getStatus())
                    .body(RsData.fail(ErrorCode.INVALID_CREDENTIALS));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RsData<>("F-500", "외부 서비스 호출 중 오류가 발생했습니다.", null));
    }

    //예상하지 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RsData<Void>> handleException(Exception e) {
        log.error("Unexpected Exception: ", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RsData<>("F-500", "서버 오류가 발생했습니다.", null));
    }
}
