package backend.mossy.boundedContext.payout.domain.payout;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
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
    @JoinColumn(name = "payer_user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutUser payer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payee_seller_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutSeller payee;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    /**
     * @Builder를 생성자에 선언하여 빌더 패턴 적용
     * 기존 검증 로직을 유지하여 안전한 객체 생성을 보장합니다.
     */
    @Builder
    public PayoutItem(Payout payout, PayoutEventType eventType, String relTypeCode, Long relId,
                      LocalDateTime paymentDate, PayoutUser payer, PayoutSeller payee, BigDecimal amount) {

        validatePayoutItem(payout, eventType, relTypeCode, relId, paymentDate, payee, amount);

        this.payout = payout;
        this.eventType = eventType;
        this.relTypeCode = relTypeCode;
        this.relId = relId;
        this.paymentDate = paymentDate;
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
    }

    private void validatePayoutItem(Payout payout, PayoutEventType eventType, String relTypeCode,
                                    Long relId, LocalDateTime paymentDate, PayoutSeller payee, BigDecimal amount) {
        if (payout == null) throw new DomainException(ErrorCode.PAYOUT_IS_NULL);
        if (eventType == null) throw new DomainException(ErrorCode.PAYOUT_EVENT_TYPE_IS_NULL);
        if (relTypeCode == null || relTypeCode.isBlank()) throw new DomainException(ErrorCode.REL_TYPE_CODE_IS_NULL);
        if (relId == null) throw new DomainException(ErrorCode.REL_ID_IS_NULL);
        if (paymentDate == null) throw new DomainException(ErrorCode.PAYMENT_DATE_IS_NULL);
        if (payee == null) throw new DomainException(ErrorCode.PAYEE_IS_NULL);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException(ErrorCode.INVALID_PAYOUT_AMOUNT);
        }
    }
}