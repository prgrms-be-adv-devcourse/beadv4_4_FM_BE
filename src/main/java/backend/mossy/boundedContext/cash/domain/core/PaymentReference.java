package backend.mossy.boundedContext.cash.domain.core;

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
    @Column(nullable = false)
    private Long buyerId;  // 구매자 ID
    @Column(unique = true)
    private String pgUid; // PG사 거래 고유 번호
}