package backend.mossy.boundedContext.payout.domain.payout;

import backend.mossy.shared.payout.dto.event.payout.CreatePayoutCandidateItemDto;
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
    private LocalDateTime paymentDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payer_user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutUser payer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payee_seller_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutSeller payee;

    @Column(name = "amount", columnDefinition = "INT DEFAULT 0")
    private BigDecimal amount;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "payout_item_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutItem payoutItem;

    @Builder
    public PayoutCandidateItem(PayoutEventType eventType, String relTypeCode, Long relId,
                               LocalDateTime paymentDate, PayoutUser payer, PayoutSeller payee, BigDecimal amount) {
        this.eventType = eventType;
        this.relTypeCode = relTypeCode;
        this.relId = relId;
        this.paymentDate = paymentDate;
        this.payer = payer;
        this.payee = payee;
        this.amount = (amount != null) ? amount : BigDecimal.ZERO;
    }

    public static PayoutCandidateItem from(CreatePayoutCandidateItemDto dto) {
        return PayoutCandidateItem.builder()
                .eventType(dto.eventType())
                .relTypeCode(dto.relTypeCode())
                .relId(dto.relId())
                .paymentDate(dto.paymentDate())
                .payer(dto.payer())
                .payee(dto.payee())
                .amount(dto.amount())
                .build();
    }

    public void setPayoutItem(PayoutItem payoutItem) {
        this.payoutItem = payoutItem;
    }
}