package backend.mossy.boundedContext.cash.domain.user;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.jpa.entity.BaseManualIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

@Entity
@Table(name = "CASH_USER_WALLET")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id", column = @Column(name = "user_wallet_id"))
public class UserWallet extends BaseManualIdAndTime {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CashUser user;

    @Column(precision = 10, scale = 3, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "wallet", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<UserCashLog> userCashLogs = new ArrayList<>();

    public UserWallet(CashUser user) {
        super(user.getId());
        this.user = user;
        this.balance = BigDecimal.ZERO;
    }

    public void credit(BigDecimal amount, UserEventType eventType, String relTypeCode, Long relId) {
        this.balance = this.balance.add(amount);
        addUserCashLog(amount, eventType, relTypeCode, relId);
    }

    public void debit(BigDecimal amount, UserEventType eventType, String relTypeCode, Long relId) {
        validateAmount(amount);

        // 추후 GlobalExceptionHandler에서 공통으로 처리할 계획
        if (this.balance.compareTo(amount) < 0) {
            throw new RuntimeException("잔액이 부족합니다.");
        }
        this.balance = this.balance.subtract(amount);
        addUserCashLog(amount.negate(), eventType, relTypeCode, relId);
    }

    private void addUserCashLog(BigDecimal amount, UserEventType eventType, String relTypeCode, Long relId) {
        UserCashLog cashLog = UserCashLog.builder()
            .wallet(this)
            .user(this.user)
            .amount(amount)
            .balance(this.balance) // 변동 후 최종 잔액 기록
            .eventType(eventType)
            .relTypeCode(relTypeCode)
            .relId(relId)
            .build();

        this.userCashLogs.add(cashLog);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("400", "잘못된 금액입니다.");
        }
    }
}
