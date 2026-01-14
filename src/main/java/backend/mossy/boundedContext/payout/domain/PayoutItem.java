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
@Getter
@Table(name = "PAYOUT_ITEM") // 스키마의 테이블명과 일치시킴
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayoutItem extends BaseIdAndTime {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payout_id", nullable = false)
    private Payout payout;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 30, nullable = false)
    private PayoutEventType eventType;

    @Column(name = "rel_type_code", length = 100, nullable = false)
    private String relTypeCode;

    @Column(name = "rel_id", nullable = false)
    private Long relId;

    /**
     * 금액: 스키마의 DECIMAL(10,2)에 맞춰 BigDecimal 사용 권장
     * 정수 기반(long)을 원하시면 1원 단위 절사가 필요합니다.
     */
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    /**
     * 실제 정산 예정일/처리일 (DB: payout_date)
     */
    @Column(name = "payout_date", nullable = false)
    private LocalDateTime payoutDate;

    @Builder
    public PayoutItem(Payout payout, Long sellerId, PayoutEventType eventType,
                      String relTypeCode, Long relId, BigDecimal amount, LocalDateTime payoutDate) {
        this.payout = payout;
        this.sellerId = sellerId;
        this.eventType = eventType;
        this.relTypeCode = relTypeCode;
        this.relId = relId;
        this.amount = amount;
        this.payoutDate = payoutDate;
    }
}