package com.mossy.boundedContext.donation.domain;

import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;

import com.mossy.global.jpa.entity.BaseIdAndTime;
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
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    /**
     * 이 기부로 인해 상쇄된 탄소량 (kg 단위)
     */
    @Column(name = "carbon_offset", nullable = false)
    private BigDecimal carbonOffset;

    /**
     * DonationLog 엔티티를 생성하는 빌더 패턴 생성자
     * 정산 완료 후에 생성되므로 생성 시점에 이미 정산 완료 상태
     *
     * @param user 기부를 한 사용자
     * @param orderItemId 관련 주문 아이템 ID
     * @param amount 기부 금액
     * @param carbonOffset 상쇄된 탄소량 (kg)
     */
    @Builder
    public DonationLog(PayoutUser user, Long orderItemId, BigDecimal amount, BigDecimal carbonOffset) {
        validateDonation(user, orderItemId, amount);
        this.user = user;
        this.orderItemId = orderItemId;
        this.amount = amount;
        this.carbonOffset = carbonOffset;
    }

    private void validateDonation(PayoutUser user, Long orderItemId, BigDecimal amount) {
        if (user == null) throw new DomainException(ErrorCode.PAYOUT_USER_NOT_FOUND);
        if (orderItemId == null) throw new DomainException(ErrorCode.ORDER_NOT_FOUND);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(ErrorCode.INVALID_DONATION_AMOUNT);
        }
    }
}
