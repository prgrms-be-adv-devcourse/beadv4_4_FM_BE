package backend.mossy.boundedContext.cash.domain.core;

import backend.mossy.global.jpa.entity.BaseManualIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PAYMENT_PAYMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "payment_id"))
public class Payment extends BaseManualIdAndTime {

    @Embedded
    private PaymentReference reference; // 복합키 (orderId + buyerId + pgUid)

    @Column(nullable = false)
    private long amount; // 결제 금액

    @Enumerated(EnumType.STRING)
    private PayMethod payMethod; // 결제 수단

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status; // 결제 상태

}
