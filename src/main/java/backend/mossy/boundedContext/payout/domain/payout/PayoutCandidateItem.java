package backend.mossy.boundedContext.payout.domain.payout;

import backend.mossy.boundedContext.payout.app.payout.PayoutAddPayoutCandidateItemsUseCase;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.payout.dto.event.payout.CreatePayoutCandidateItemDto;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

/**
 * [Domain Entity] 정산 후보 아이템을 나타내는 엔티티입니다.
 * 이 엔티티는 특정 금융 거래(예: 상품 판매 대금, 수수료, 기부금 등)가 향후 정산(Payout)에 포함될 예정임을
 * 기록하는 임시 레코드 역할을 합니다. {@link PayoutAddPayoutCandidateItemsUseCase}에 의해 생성됩니다.
 */
@Entity
@Getter
@Table(name = "PAYOUT_CANDIDATE") // 정산 후보 아이템을 저장하는 테이블
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayoutCandidateItem extends BaseIdAndTime {

    /**
     * 이 정산 후보 아이템의 성격(이벤트 타입)을 정의
     * 예: 상품 판매 대금, 수수료, 기부금 등.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 30, nullable = false)
    private PayoutEventType eventType;

    /**
     * 이 정산 후보 아이템이 참조하는 외부 엔티티의 타입 코드
     * 예: "ORDER_ITEM"
     */
    @Column(name = "rel_type_code", length = 100, nullable = false)
    private String relTypeCode;

    /**
     * 이 정산 후보 아이템이 참조하는 외부 엔티티의 ID
     * 예: OrderItem의 ID
     */
    @Column(name = "rel_id", nullable = false)
    private Long relId;

    /**
     * 이 정산 후보 아이템이 발생한 결제 일시
     * 정산 대기 기간 등을 계산하는 데 사용
     */
    @Column(name = "payout_date", nullable = false) // 'payout_date'는 실제 정산일이 아니라 결제 발생일
    private LocalDateTime paymentDate;

    /**
     * 이 정산 후보 아이템에 대한 지불을 담당하는 주체(예: 구매자)
     * PayoutUser 엔티티와의 다대일(ManyToOne) 관계를 가짐
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payer_user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutUser payer;

    /**
     * 이 정산 후보 아이템에 대한 금액을 수취하는 주체(예: 판매자, 시스템)
     * PayoutSeller 엔티티와의 다대일(ManyToOne) 관계를 가짐
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payee_seller_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutSeller payee;

    /**
     * 이 정산 후보 아이템의 금액
     */
    @Column(name = "amount", columnDefinition = "INT DEFAULT 0")
    private BigDecimal amount;

    /**
     * 이 정산 후보 아이템이 실제 정산(Payout)에 포함되어 {@link PayoutItem}으로 변환되었을 때,
     * 해당 PayoutItem을 참조합니다. 이 필드가 채워지면 더 이상 정산 후보가 아님을 나타냄
     * PayoutItem 엔티티와의 일대일(OneToOne) 관계를 가짐
     */
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "payout_item_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutItem payoutItem;

    /**
     * PayoutCandidateItem 엔티티를 생성하는 빌더 패턴 생성자
     *
     * @param eventType   정산 이벤트 타입
     * @param relTypeCode 관련 엔티티 타입 코드
     * @param relId       관련 엔티티 ID
     * @param paymentDate 결제 발생일
     * @param payer       지불자 (PayoutUser)
     * @param payee       수취인 (PayoutSeller)
     * @param amount      금액
     */
    @Builder
    public PayoutCandidateItem(PayoutEventType eventType, String relTypeCode, Long relId,
                               LocalDateTime paymentDate, PayoutUser payer, PayoutSeller payee, BigDecimal amount) {

        if (eventType == null) {
            throw new DomainException(ErrorCode.PAYOUT_EVENT_TYPE_IS_NULL); // status: 400
        }
        if (relTypeCode == null || relTypeCode.isBlank()) {
            throw new DomainException(ErrorCode.REL_TYPE_CODE_IS_NULL); // status: 400
        }
        if (relId == null) {
            throw new DomainException(ErrorCode.REL_ID_IS_NULL); // status: 400
        }
        if (paymentDate == null) {
            throw new DomainException(ErrorCode.PAYMENT_DATE_IS_NULL); // status: 400
        }
        if (payee == null) {
            throw new DomainException(ErrorCode.PAYEE_IS_NULL); // status: 400
        }
        if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException(ErrorCode.INVALID_AMOUNT); // status: 400
        }
        this.eventType = eventType;
        this.relTypeCode = relTypeCode;
        this.relId = relId;
        this.paymentDate = paymentDate;
        this.payer = payer;
        this.payee = payee;
        this.amount = (amount != null) ? amount : BigDecimal.ZERO;
    }

    /**
     * CreatePayoutCandidateItemDto 객체로부터 PayoutCandidateItem 엔티티를 생성하는 팩토리 메서드
     *
     * @param dto 정산 후보 아이템 생성 DTO
     * @return 생성된 PayoutCandidateItem 엔티티
     */
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

    /**
     * 이 정산 후보 아이템이 실제 정산 {@link PayoutItem}으로 처리되었을 때, 해당 PayoutItem과 연결
     * 이 연결을 통해 후보 아이템이 중복 처리되지 않도록 관리
     *
     * @param payoutItem 연결될 PayoutItem 객체
     */
    public void setPayoutItem(PayoutItem payoutItem) {
        this.payoutItem = payoutItem;
    }
}