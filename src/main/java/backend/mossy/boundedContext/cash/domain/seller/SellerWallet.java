package backend.mossy.boundedContext.cash.domain.seller;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.jpa.entity.BaseManualIdAndTime;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CASH_SELLER_WALLET")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "seller_wallet_id"))
public class SellerWallet extends BaseManualIdAndTime {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CashSeller seller;

    @Column(precision = 10, scale = 3, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "wallet", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<SellerCashLog> sellerCashLogs = new ArrayList<>();

    @Builder
    public SellerWallet(CashSeller seller) {
        super(seller.getId());
        this.seller = seller;
        this.balance = BigDecimal.ZERO;
    }

    // 입금/수익으로 사용
    public void credit(BigDecimal amount, SellerEventType eventType, String relTypeCode, Long relId) {
        this.balance = this.balance.add(amount);
        addSellerCashLog(amount, eventType, relTypeCode, relId);
    }

    // 출금할 때 사용
    public void debit(BigDecimal amount, SellerEventType eventType, String relTypeCode, Long relId) {
        validateAmount(amount);

        // 추후 GlobalExceptionHandler에서 공통으로 처리할 계획
        if (this.balance.compareTo(amount) < 0) {
            throw new DomainException("400","출금 가능한 잔액이 부족합니다.");
        }
        this.balance = this.balance.subtract(amount);
        addSellerCashLog(amount.negate(), eventType, relTypeCode, relId);
    }

    private void addSellerCashLog(BigDecimal amount, SellerEventType eventType, String relTypeCode, Long relId) {
        SellerCashLog cashLog = SellerCashLog.builder()
            .wallet(this)
            .user(this.seller)
            .amount(amount)
            .balance(this.balance) // 변동 후 최종 잔액 기록
            .eventType(eventType)
            .relTypeCode(relTypeCode)
            .relId(relId)
            .build();

        this.sellerCashLogs.add(cashLog);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("400", "잘못된 금액입니다.");
        }
    }
}