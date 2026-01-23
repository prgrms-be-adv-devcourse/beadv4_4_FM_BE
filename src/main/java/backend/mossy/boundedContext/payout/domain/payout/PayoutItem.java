package backend.mossy.boundedContext.payout.domain.payout;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

/**
 * [Domain Entity] 개별 정산 항목을 나타내는 엔티티
 * {@link Payout}에 포함되는 하나의 상세 거래 내역을 의미하며,
 * 어떤 이벤트로 인해 누가 누구에게 얼마를 지급했는지를 기록
 */
@Entity
@Table(name = "PAYOUT_PAYOUT_ITEM") // Payout 테이블과 구분을 위해 PayoutItem 테이블은 PAYOUT_PAYOUT_ITEM으로 명명
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayoutItem extends BaseIdAndTime {

    /**
     * 이 정산 항목이 속한 주 정산(Payout)을 참조합니다.
     * {@link Payout} 엔티티와의 다대일(ManyToOne) 관계를 가짐
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payout_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Payout payout;

    /**
     * 이 정산 항목의 성격(이벤트 타입)을 정의
     * 예: 상품 판매 대금, 수수료, 기부금 등.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 30, nullable = false)
    private PayoutEventType eventType;

    /**
     * 이 정산 항목이 참조하는 외부 엔티티의 타입 코드
     * 예를 들어, 'ORDER_ITEM'
     */
    @Column(name = "rel_type_code", length = 100, nullable = false)
    private String relTypeCode;

    /**
     * 이 정산 항목이 참조하는 외부 엔티티의 ID
     * 예를 들어, OrderItem의 ID.
     */
    @Column(name = "rel_id", nullable = false)
    private Long relId;

    /**
     * 이 정산 항목의 실제 결제가 발생한 일시
     */
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    /**
     * 이 정산 항목에 대한 지불을 담당하는 주체(예: 구매자)
     * {@link PayoutUser} 엔티티와의 다대일(ManyToOne) 관계를 가짐
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payer_user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutUser payer;

    /**
     * 이 정산 항목에 대한 금액을 수취하는 주체(예: 판매자, 시스템)
     * {@link PayoutSeller} 엔티티와의 다대일(ManyToOne) 관계를 가짐
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payee_seller_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutSeller payee;

    /**
     * 이 정산 항목의 금액
     */
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    /**
     * PayoutItem 엔티티를 생성하는 생성자
     *
     * @param payout      이 항목이 속할 주 Payout
     * @param eventType   정산 이벤트 타입
     * @param relTypeCode 관련 엔티티 타입 코드
     * @param relId       관련 엔티티 ID
     * @param payDate     결제 발생일
     * @param payer       지불자 (PayoutUser)
     * @param payee       수취인 (PayoutSeller)
     * @param amount      금액
     */
    public PayoutItem(Payout payout, PayoutEventType eventType, String relTypeCode, Long relId, LocalDateTime payDate, PayoutUser payer, PayoutSeller payee, BigDecimal amount) {
        if (payout == null) {
            throw new DomainException(ErrorCode.PAYOUT_IS_NULL);
        }
        if (eventType == null) {
            throw new DomainException(ErrorCode.PAYOUT_EVENT_TYPE_IS_NULL);
        }
        if (relTypeCode == null || relTypeCode.isBlank()) {
            throw new DomainException(ErrorCode.REL_TYPE_CODE_IS_NULL);
        }
        if (relId == null) {
            throw new DomainException(ErrorCode.REL_ID_IS_NULL);
        }
        if (payDate == null) {
            throw new DomainException(ErrorCode.PAYMENT_DATE_IS_NULL);
        }
        if (payee == null) {
            throw new DomainException(ErrorCode.PAYEE_IS_NULL);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException(ErrorCode.INVALID_AMOUNT);
        }
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