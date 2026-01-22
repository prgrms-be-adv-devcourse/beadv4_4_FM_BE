package backend.mossy.boundedContext.payout.domain.donation;

import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;

/**
 * [Domain Entity] 개별 기부 내역을 기록하는 엔티티
 * 어떤 사용자가 어떤 주문 아이템을 통해 얼마를 기부했고, 얼마나 탄소를 상쇄했는지,
 * 그리고 이 기부가 정산 처리가 완료되었는지 여부 등을 관리
 */
@Entity
@Table(name = "DONATION_LOGS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DonationLog extends BaseIdAndTime {

    /**
     * 기부를 한 사용자(구매자)를 나타냅니다.
     * PayoutUser 엔티티와의 다대일(ManyToOne) 관계를 가짐
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutUser user;

    /**
     * 이 기부 로그가 발생한 원인이 되는 주문 아이템의 ID
     */
    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    /**
     * 기부된 금액 또는 탄소 상쇄를 위해 사용된 금액
     */
    @Column(name = "amount", nullable = false, columnDefinition = "INT COMMENT '기부/오프셋 금액'")
    private BigDecimal amount;

    /**
     * 이 기부로 인해 상쇄된 탄소량(그램 단위)
     */
    @Column(name = "carbon_offset_g", nullable = false, columnDefinition = "DOUBLE COMMENT '상쇄된 탄소량(그램)'")
    private Double carbonOffsetG;

    /**
     * 이 기부 로그가 월말 정산 프로세스에 반영되어 처리 완료되었는지 여부를 나타냄
     * 기본값은 false이며, 정산이 완료되면 true로 변경
     */
    @Column(name = "is_settled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE COMMENT '월말 정산 반영 여부'")
    private Boolean isSettled = false;

    /**
     * DonationLog 엔티티를 생성하는 빌더 패턴 생성자
     * @param user 기부를 한 사용자
     * @param orderItemId 관련 주문 아이템 ID
     * @param amount 기부 금액
     * @param carbonOffsetG 상쇄된 탄소량(그램)
     */
    @Builder
    public DonationLog(PayoutUser user, Long orderItemId, BigDecimal amount, Double carbonOffsetG) {
        this.user = user;
        this.orderItemId = orderItemId;
        this.amount = amount;
        this.carbonOffsetG = carbonOffsetG;
        this.isSettled = false; // 새로 생성된 기부 로그는 기본적으로 정산되지 않은 상태입니다.
    }

    /**
     * 이 기부 로그의 정산 상태를 '정산 완료(true)'로 변경
     * Payout 프로세스에 의해 호출
     */
    public void settle() {
        this.isSettled = true;
    }
}
