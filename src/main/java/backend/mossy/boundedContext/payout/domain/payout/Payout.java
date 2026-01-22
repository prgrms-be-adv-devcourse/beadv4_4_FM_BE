package backend.mossy.boundedContext.payout.domain.payout;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import backend.mossy.shared.payout.dto.event.payout.PayoutEventDto;
import backend.mossy.shared.payout.event.PayoutCompletedEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

/**
 * [Domain Entity] 정산(Payout) 트랜잭션의 메인 엔티티
 * 특정 수취인(Payee)에게 지급될 정산 내역의 총합을 관리하며,
 * 여러 {@link PayoutItem}들을 포함하고 정산 완료 여부와 시점을 기록
 */
@Entity
@Table(name = "PAYOUT_PAYOUT") // 테이블 이름 PAYOUT과 구분을 위해 PAYOUT_PAYOUT으로 명명
@NoArgsConstructor
@Getter
public class Payout extends BaseIdAndTime {

    /**
     * 이 정산을 받을 수취인(Payee)입니다. (판매자, 시스템 등)
     * PayoutSeller 엔티티와의 다대일(ManyToOne) 관계를 가짐
     */
    @ManyToOne(fetch = LAZY)
    private PayoutSeller payee;

    /**
     * 정산이 완료된 일시를 기록(정산이 완료되지 않았다면 null)
     * 정산 완료 여부를 판단하는 중요한 기준
     */
    @Setter
    private LocalDateTime payoutDate;

    /**
     * 이 정산의 총 금액입니다. 모든 PayoutItem의 금액 합계
     */
    private BigDecimal amount;

    /**
     * 이 정산에 포함된 개별 정산 항목들(예: 상품 판매 대금, 수수료 등)의 리스트
     * Payout과 PayoutItem은 일대다(OneToMany) 관계를 가짐
     * Payout 엔티티 저장/삭제 시 PayoutItem도 함께 처리됩니다 (CascadeType.PERSIST, REMOVE).
     */
    @OneToMany(mappedBy = "payout", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<PayoutItem> items = new ArrayList<>();

    /**
     * 새로운 Payout 엔티티를 생성하는 생성자
     *
     * @param payee 이 정산의 수취인이 될 PayoutSeller 객체
     */
    public Payout(PayoutSeller payee) {
        this.payee = payee;
        this.amount = BigDecimal.ZERO; // 초기 정산 금액은 0으로 설정됩니다.
    }

    /**
     * 이 Payout에 새로운 PayoutItem을 추가
     * PayoutItem이 추가되면 이 Payout의 총 금액(amount)도 함께 업데이트
     *
     * @param eventType 정산 항목의 이벤트 타입 (예: 상품 판매 대금, 수수료)
     * @param relTypeCode 관련 엔티티의 타입 코드
     * @param relId 관련 엔티티의 ID
     * @param payDate 결제 발생일
     * @param payer 지불자
     * @param payee 수취인 (이 Payout의 payee와 동일해야 함)
     * @param amount 정산 항목의 금액
     * @return 생성된 PayoutItem 객체
     */
    public PayoutItem addItem(
            PayoutEventType eventType,
            String relTypeCode,
            Long relId,
            LocalDateTime payDate,
            PayoutUser payer,
            PayoutSeller payee, BigDecimal amount
    ) {
        PayoutItem payoutItem = PayoutItem.builder()
                .payout(this)            // 현재 Payout과 연결
                .eventType(eventType)
                .relTypeCode(relTypeCode)
                .relId(relId)
                .paymentDate(payDate)
                .payer(payer)
                .payee(this.payee)       // 이 Payout의 수취인으로 설정
                .amount(amount)
                .build();

        items.add(payoutItem);

        // 총 정산 금액을 업데이트
        if (this.amount == null) {
            this.amount = BigDecimal.ZERO;
        }
        this.amount = this.amount.add(amount);

        return payoutItem;
    }

    /**
     * 이 Payout을 '완료' 상태로 처리
     * 현재 일시를 payoutDate로 설정하고, PayoutCompletedEvent를 발행하여 후속 처리를 트리거
     */
    public void completePayout() {
        this.payoutDate = LocalDateTime.now(); // 정산 완료 일시 기록
        publishEvent(
                new PayoutCompletedEvent(
                        toDto() // 이벤트에 포함될 Payout 정보를 DTO로 변환하여 전달
                )
        );
    }

    /**
     * 현재 Payout 엔티티의 핵심 정보를 담은 DTO로 변환하여 반환
     * 주로 이벤트 발행 시 이벤트 데이터로 활용
     * @return Payout의 핵심 정보를 담은 PayoutEventDto
     */
    public PayoutEventDto toDto() {
        return PayoutEventDto.builder()
                .id(getId())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .payeeId(payee.getId())
                .payeeNickname(payee.getStoreName())
                .payoutDate(payoutDate)
                .amount(amount)
                .isSystem(payee.isSystem()) // 수취인이 시스템인지 여부
                .build();
    }
}