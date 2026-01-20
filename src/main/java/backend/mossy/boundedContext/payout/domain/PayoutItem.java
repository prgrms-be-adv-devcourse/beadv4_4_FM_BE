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
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayoutItem extends BaseIdAndTime {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payout_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Payout payout;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 30, nullable = false)
    private PayoutEventType eventType;

    @Column(name = "rel_type_code", length = 100, nullable = false)
    private String relTypeCode;

    @Column(name = "rel_id", nullable = false)
    private Long relId;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payer_seller_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutSeller payer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payee_seller_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutSeller payee;

    @Column(name = "amount", nullable = false)
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