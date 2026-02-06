package com.mossy.member.domain.payout;

import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.payout.dto.event.payout.PayoutEventDto;
import com.mossy.shared.payout.enums.PayoutEventType;
import com.mossy.shared.payout.event.PayoutCompletedEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "PAYOUT_PAYOUT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payout extends BaseIdAndTime {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "payee_id")
    private PayoutSeller payee;

    @Column(name = "payout_date")
    private LocalDateTime payoutDate;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "payout", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<PayoutItem> items = new ArrayList<>();

    public Payout(PayoutSeller payee) {
        if (payee == null) throw new DomainException(ErrorCode.PAYOUT_SELLER_NOT_FOUND);
        this.payee = payee;
        this.amount = BigDecimal.ZERO;
    }

    /**
     * 이 Payout에 새로운 PayoutItem을 추가 (빌더 패턴 적용)
     */
    public PayoutItem addItem(
            PayoutEventType eventType,
            String relTypeCode,
            Long relId,
            LocalDateTime payDate,
            PayoutUser payer,
            PayoutSeller payee,
            BigDecimal amount
    ) {
        // 1. 상태 검증
        if (isCompleted()) {
            throw new DomainException(ErrorCode.ALREADY_COMPLETED_PAYOUT);
        }

        // 2. 금액 검증
        if (this.amount == null || this.amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException(ErrorCode.INVALID_PAYOUT_AMOUNT);
        }

        // 3. PayoutItem 빌더 패턴 적용
        PayoutItem payoutItem = PayoutItem.builder()
                .payout(this)
                .eventType(eventType)
                .relTypeCode(relTypeCode)
                .relId(relId)
                .paymentDate(payDate)
                .payer(payer)
                .payee(this.payee) // 현재 정산서의 수취인으로 고정
                .amount(amount)
                .build();

        this.items.add(payoutItem);

        // 4. 총 정산 금액 업데이트 (필드 초기화 덕분에 null 체크 생략 가능)
        this.amount = this.amount.add(amount);

        return payoutItem;
    }

    /**
     * 이 Payout을 '완료' 상태로 처리
     */
    public void completePayout() {
        if (isCompleted()) {
            throw new DomainException(ErrorCode.ALREADY_COMPLETED_PAYOUT);
        }

        this.payoutDate = LocalDateTime.now();

        publishEvent(
                new PayoutCompletedEvent(
                        toDto()
                )
        );
    }

    public boolean isCompleted() {
        return this.payoutDate != null;
    }

    public PayoutEventDto toDto() {
        return PayoutEventDto.builder()
                .id(getId())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .payeeId(payee.getId())
                .payeeNickname(payee.getStoreName())
                .payoutDate(payoutDate)
                .amount(amount)
                .isSystem(payee.isSystem())
                .build();
    }
}