package backend.mossy.boundedContext.payout.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "PAYOUT_CANDIDATE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayoutCandidateItem extends BaseIdAndTime {

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 30, nullable = false)
    private PayoutEventType eventType;

    @Column(name = "rel_type_code", length = 100, nullable = false)
    private String relTypeCode;

    @Column(name = "rel_id", nullable = false)
    private Long relId;

    @Column(name = "payout_date", nullable = false)
    private LocalDateTime payoutDate;

    /**
     * 돈을 받을 주체 (Seller)만 남김
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seller_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutSeller payee;

    @Column(name = "amount", columnDefinition = "INT DEFAULT 0")
    private BigDecimal amount;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "payout_item_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutItem payoutItem;

    @Builder
    public PayoutCandidateItem(PayoutEventType eventType, String relTypeCode, Long relId,
                               LocalDateTime payoutDate, PayoutSeller payee, BigDecimal amount) {
        this.eventType = eventType;
        this.relTypeCode = relTypeCode;
        this.relId = relId;
        this.payoutDate = payoutDate;
        this.payee = payee;
        this.amount = (amount != null) ? amount : BigDecimal.ZERO;
    }
}