package backend.mossy.boundedContext.payout.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import backend.mossy.shared.payout.dto.response.PayoutResponseDto;
import jakarta.persistence.*;
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
@Table(name = "PAYOUT")
@NoArgsConstructor
@Getter
public class Payout extends BaseIdAndTime {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seller_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private PayoutUser payee;

    private LocalDateTime payoutDate;

    private BigDecimal amount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "payout", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<PayoutItem> items = new ArrayList<>();

    public Payout(PayoutUser payee) {
        this.payee = payee;
    }

    public PayoutItem addItem(PayoutEventType eventType, String relTypeCode, Long relId, LocalDateTime payDate, Long itemAmount) {
        PayoutItem payoutItem = PayoutItem.builder()
                .payout(this)
                .sellerId(this.payee.getId())
                .eventType(eventType)
                .relTypeCode(relTypeCode)
                .relId(relId)
                .amount(BigDecimal.valueOf(itemAmount))
                .payoutDate(payDate)
                .build();

        this.items.add(payoutItem);

        // 총 정산 금액을 업데이트합니다.
        this.amount = this.amount.add(payoutItem.getAmount());

        return payoutItem;
    }

    public void completePayout() {
        this.payoutDate = LocalDateTime.now();

        // 정산 완료 이벤트 발행 로직 등이 올 자리
    }

}