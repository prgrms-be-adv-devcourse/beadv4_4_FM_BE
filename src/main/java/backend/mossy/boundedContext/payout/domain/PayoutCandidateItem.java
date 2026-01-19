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

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    /**
     * 돈을 지불하는 주체 (Buyer)
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private PayoutSeller payer;

    /**
     * 돈을 받을 주체 (Seller)
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private PayoutSeller payee;

    @Column(name = "amount", columnDefinition = "INT DEFAULT 0")
    private BigDecimal amount;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "payout_item_id")
    private PayoutItem payoutItem;

    @Builder
    public PayoutCandidateItem(PayoutEventType eventType, String relTypeCode, Long relId,
                               LocalDateTime paymentDate, PayoutSeller payer, PayoutSeller payee, BigDecimal amount) {
        this.eventType = eventType;
        this.relTypeCode = relTypeCode;
        this.relId = relId;
        this.paymentDate = paymentDate;
        this.payer = payer;
        this.payee = payee;
        this.amount = (amount != null) ? amount : BigDecimal.ZERO;
    }

    /**
     * PayoutItem과의 연결을 설정합니다.
     * 패키지 내부에서만 호출 가능하며, Payout.addItem()에서 자동으로 처리됩니다.
     */
    void linkToPayoutItem(PayoutItem payoutItem) {
        if (this.payoutItem != null) {
            throw new IllegalStateException("이미 처리된 정산 후보 항목입니다.");
        }
        this.payoutItem = payoutItem;
    }

}