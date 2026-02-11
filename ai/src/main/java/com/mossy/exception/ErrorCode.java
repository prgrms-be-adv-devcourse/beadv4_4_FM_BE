package com.mossy.exception;

import com.mossy.global.exception.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseCode {

    // =============================================================
    // === Recommendation
    // =============================================================
    ;

    private final int status;
    private final String msg;
}
