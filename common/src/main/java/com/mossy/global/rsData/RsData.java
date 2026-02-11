package com.mossy.global.rsData;

import com.mossy.global.exception.BaseCode;
import com.mossy.standard.resultType.ResultType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
@JsonPropertyOrder({"resultCode", "msg", "data"})
public class RsData<T> implements ResultType {
    private final String resultCode;
    private final String msg;
    private final T data;

    // 데이터 없는 응답 생성자(원래있던거)
    public RsData(String resultCode, String msg) {
        this(resultCode, msg, null);
    }

    public static <T> RsData<T> success(BaseCode successCode) {
        return new RsData<>("S-" + successCode.getStatus(), successCode.getMsg(), null);
    }

    public static <T> RsData<T> success(BaseCode successCode, T data) {
        return new RsData<>("S-" + successCode.getStatus(), successCode.getMsg(), data);
    }

    //실패할 경우
    public static<T> RsData<T> fail(BaseCode errorCode) {
        return new RsData<>(
                "F-" + errorCode.getStatus(),
                errorCode.getMsg(),
                null
        );
    }
    public static<T> RsData<T> fail(BaseCode errorCode, T data) {
        return new RsData<>(
            "F-" + errorCode.getStatus(),
            errorCode.getMsg(),
            data
        );
    }
}
