package com.mossy.exception;

import com.mossy.global.rsData.RsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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

    // DB 제약 조건 위반 - 한글 메시지가 헤더에 들어가지 않도록 명시적 처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RsData<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new RsData<>("F-409", "데이터 중복 또는 제약 조건 위반입니다.", null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RsData<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RsData<>("F-400", e.getMessage(), null));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<RsData<Void>> handleIllegalStateException(IllegalStateException e) {
        log.warn("IllegalStateException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new RsData<>("F-409", e.getMessage(), null));
    }

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RsData<Void>> handleException(Exception e) {
        log.error("Unexpected Exception: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RsData<>("F-500", "서버 오류가 발생했습니다.", null));
    }
}
