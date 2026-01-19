package backend.mossy.boundedContext.payout.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "PAYOUT_PAYOUT_ITEM")
@NoArgsConstructor
public class PayoutItem extends BaseIdAndTime {
    /**
     * 이 정산 항목이 속한 전체 정산(Payout) 객체
     */
    @ManyToOne(fetch = LAZY)
    private Payout payout;

    @Enumerated(EnumType.STRING)
    private PayoutEventType eventType;

    String relTypeCode;

    private Long relId;

    private LocalDateTime paymentDate;

    @ManyToOne(fetch = LAZY)
    private PayoutSeller payer;

    @ManyToOne(fetch = LAZY)
    private PayoutSeller payee;

    private BigDecimal amount;

    public PayoutItem(Payout payout, PayoutEventType eventType, String relTypeCode, Long relId, LocalDateTime payDate, PayoutSeller payer, PayoutSeller payee, BigDecimal amount) {
        this.payout = payout;
        this.eventType = eventType;
        this.relTypeCode = relTypeCode;
        this.relId = relId;
        this.paymentDate = payDate;
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
    }
}