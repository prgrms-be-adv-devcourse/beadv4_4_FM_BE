package backend.mossy.standard.resultType;

public interface ResultType {
    String getResultCode();

    String getMsg();

    default <T> T getData() {
        return null;
    }
}
