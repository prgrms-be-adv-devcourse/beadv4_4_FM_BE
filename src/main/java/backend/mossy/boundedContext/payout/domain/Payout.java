package backend.mossy.boundedContext.payout.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal; // BigDecimal 임포트 추가
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "PAYOUT") // SQL 스키마에 맞춰 테이블 이름 변경
@NoArgsConstructor
@Getter
public class Payout extends BaseIdAndTime {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seller_id") // seller_id 컬럼과 매핑
    private PayoutMember payee;


    @Setter
    private LocalDateTime payoutDate;

    private BigDecimal amount = BigDecimal.ZERO; // BigDecimal로 타입 변경 및 초기화

    @OneToMany(mappedBy = "payout", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<PayoutItem> items = new ArrayList<>();

    public Payout(PayoutMember payee) {
        this.payee = payee;
    }

    public PayoutItem addItem(PayoutEventType eventType, String relTypeCode, int relId, LocalDateTime payDate, PayoutMember payer,
                              PayoutMember payee, long itemAmount) {
        PayoutItem payoutItem = new PayoutItem(
                this, eventType, relTypeCode, relId, payDate, payer, payee, itemAmount
        );

        items.add(payoutItem);

        // 총 정산 금액을 업데이트합니다. PayoutItem의 long amount를 BigDecimal로 변환하여 더합니다.
        this.amount = this.amount.add(BigDecimal.valueOf(itemAmount));

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


    public PayoutDto toDto() {
        return new PayoutDto(
                getId(),
                getCreateDate(),
                getModifyDate(),
                payee.getId(),
                payee.getNickname(),
                payoutDate,
                amount, // BigDecimal 타입으로 변경된 amount 전달
                payee.isSystem()
                // buyer.getId() // buyer ID 제거
        );
    }
}