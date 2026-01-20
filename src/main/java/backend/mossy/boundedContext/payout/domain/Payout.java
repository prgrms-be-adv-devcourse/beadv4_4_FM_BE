package backend.mossy.boundedContext.payout.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import backend.mossy.shared.payout.dto.event.PayoutEventDto;
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

@Entity
@Table(name = "PAYOUT_PAYOUT")
@NoArgsConstructor
@Getter
public class Payout extends BaseIdAndTime {

    @ManyToOne(fetch = LAZY)
    private PayoutSeller payee;

    @Setter
    private LocalDateTime payoutDate;

    private BigDecimal amount;

    @OneToMany(mappedBy = "payout", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<PayoutItem> items = new ArrayList<>();

    public Payout(PayoutSeller payee) {
        this.payee = payee;
        this.amount = BigDecimal.ZERO;
    }

    public PayoutItem addItem(PayoutEventType eventType, String relTypeCode, Long relId, LocalDateTime payDate, PayoutSeller payer, PayoutSeller payee, BigDecimal amount) {
        PayoutItem payoutItem = new PayoutItem(
                this, eventType, relTypeCode, relId, payDate, payer, payee, amount
        );

        items.add(payoutItem);

        // 총 정산 금액을 업데이트합니다.
        if (this.amount == null) {
            this.amount = BigDecimal.ZERO;
        }
        this.amount = this.amount.add(amount);

        return payoutItem;
    }

    public void completePayout() {
        this.payoutDate = LocalDateTime.now();
        publishEvent(
                new PayoutCompletedEvent(
                        toDto()
                )
        );
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