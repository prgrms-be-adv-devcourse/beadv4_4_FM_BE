package backend.mossy.boundedContext.payout.domain.donation;

import backend.mossy.boundedContext.payout.domain.PayoutUser;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;

/**
 * 기부 내역 로그
 */
@Entity
@Table(name = "DONATION_LOGS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DonationLog extends BaseIdAndTime {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutUser user;

    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @Column(name = "amount", nullable = false, columnDefinition = "INT COMMENT '기부/오프셋 금액'")
    private BigDecimal amount;

    @Column(name = "carbon_offset_g", nullable = false, columnDefinition = "DOUBLE COMMENT '상쇄된 탄소량(그램)'")
    private Double carbonOffsetG;

    @Column(name = "is_settled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE COMMENT '월말 정산 반영 여부'")
    private Boolean isSettled = false;

    @Builder
    public DonationLog(PayoutUser user, Long orderItemId, BigDecimal amount, Double carbonOffsetG) {
        this.user = user;
        this.orderItemId = orderItemId;
        this.amount = amount;
        this.carbonOffsetG = carbonOffsetG;
        this.isSettled = false;
    }

    /**
     * 정산 완료 처리
     */
    public void settle() {
        this.isSettled = true;
    }
}
