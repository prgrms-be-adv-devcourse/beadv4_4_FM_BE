package backend.mossy.boundedContext.market.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class PaymentReference implements Serializable {

    @Column(nullable = false)
    private Long orderId; // 주문 ID
    @Column(unique = true)
    private String paymentKey; // PG사 거래 고유 번호
}