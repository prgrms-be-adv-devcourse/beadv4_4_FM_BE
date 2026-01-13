package backend.mossy.boundedContext.cash.domain.payment;

import backend.mossy.global.jpa.entity.BaseManualIdAndTime;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PAYMENT_PAYMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseManualIdAndTime {

    @Embedded
    private PaymentReference reference; // 복합키 (orderId + buyerId)

    @Column(unique = true)
    private String pgUid; // PG사 거래 고유 번호

    @Column(nullable = false)
    private long amount; // 결제 금액

    @Enumerated(EnumType.STRING)
    private PayMethod payMethod; // 결제 수단

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status; // 결제 상태

}
