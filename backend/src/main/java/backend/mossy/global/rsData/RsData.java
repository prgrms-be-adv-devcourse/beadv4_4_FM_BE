package backend.mossy.global.rsData;

import backend.mossy.global.exception.ErrorCode;
import backend.mossy.standard.resultType.ResultType;
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

    //성공할 경우
    public static <T> RsData<T> success(String msg, T data) {
        return new RsData<>("S-200", msg, data);
    }

    public static <T> RsData<T> success(String msg) {
        return success(msg, null);
    }

    //실패할 경우
    public static<T> RsData<T> fail(ErrorCode errorCode) {
        return new RsData<>(
                "F-" + errorCode.getStatus(),
                errorCode.getMsg(),
                null
        );
    }

    public static <T> RsData<T> fail(String resultCode, String msg) {
        return new RsData<>(resultCode, msg, null);
    }

}
