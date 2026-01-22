package backend.mossy.standard.ut;

import java.util.UUID;

public class OrderUtils {

    private static final String SEPARATOR = "__";

    public static String generateOrderNo() {
        return "ORD_" + UUID.randomUUID().toString();
    }

    public static String createPgOrderId(String originalOrderNo) {
        return originalOrderNo + SEPARATOR + UUID.randomUUID().toString().substring(0, 8);
    }


    public static String resolveOriginalOrderNo(String pgOrderId) {
        if (pgOrderId == null) return null;
        int index = pgOrderId.indexOf("__");
        if (index > 0) {
            return pgOrderId.substring(0, index); // 구분자가 있을 때만 자름
        }
        return pgOrderId; // 구분자가 없으면 받은 값 그대로 반환 (프론트 수정 전 대응용)
    }
}