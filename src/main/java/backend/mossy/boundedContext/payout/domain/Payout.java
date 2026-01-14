package backend.mossy.boundedContext.payout.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import backend.mossy.shared.payout.dto.response.PayoutItemResponseDto;
import backend.mossy.shared.payout.dto.response.PayoutResponseDto;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public PayoutItem addItem(PayoutEventType eventType, String relTypeCode, Long relId, LocalDateTime payDate, PayoutMember payer,
                              PayoutMember payee, Long itemAmount) {
        PayoutItem payoutItem = PayoutItem.builder()
                .payout(this)
                .sellerId(payee.getId())
                .eventType(eventType)
                .relTypeCode(relTypeCode)
                .relId(relId)
                .amount(BigDecimal.valueOf(itemAmount))
                .payoutDate(payDate)
                .build();

        items.add(payoutItem);

        // 총 정산 금액을 업데이트합니다.
        this.amount = this.amount.add(payoutItem.getAmount());

        return payoutItem;
    }


    public void completePayout() {
        this.payoutDate = LocalDateTime.now();

        // TODO: PayoutCompletedEvent가 새로운 PayoutResponseDto를 받도록 수정해야 합니다.
        // publishEvent(
        //         new PayoutCompletedEvent(
        //                 toDto()
        //         )
        // );
    }


    public PayoutResponseDto toDto() {
        // PayoutItem 리스트를 PayoutItemResponseDto 리스트로 변환
        List<PayoutItemResponseDto> itemDtos = this.items.stream()
                .map(item -> PayoutItemResponseDto.builder()
                        .payoutItemId(item.getId())
                        .eventType(item.getEventType().name())
                        .amount(item.getAmount())
                        .itemPayDate(item.getPayoutDate())
                        .build())
                .collect(Collectors.toList());

        // 최종적으로 PayoutResponseDto를 빌더로 생성하여 반환
        return PayoutResponseDto.builder()
                .payoutId(getId())
                .createdDate(getCreateDate())
                .payoutDate(this.payoutDate)
                .payee(this.payee.toDto()) // PayoutMember의 toDto()를 재사용
                .totalAmount(this.amount)
                .items(itemDtos)
                .build();
    }
}
