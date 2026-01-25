package backend.mossy.shared.market.dto.toss;

import java.math.BigDecimal;
import lombok.Builder;

//PG
@Builder
public record TossConfirmRequest(
    String paymentKey,
    String orderId,
    Long amount  //토스페이먼츠 승인 API에서 amount = 정수타입
) {
    public static TossConfirmRequest of(String paymentKey, String orderId, BigDecimal amount) {
        return new TossConfirmRequest(
            paymentKey,
            orderId,
            amount.longValue()
        );
    }
}
