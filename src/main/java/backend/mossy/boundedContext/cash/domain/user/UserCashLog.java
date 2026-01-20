package backend.mossy.boundedContext.cash.domain.user;

import static jakarta.persistence.FetchType.LAZY;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CASH_USER_LOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "cash_log_id"))
public class UserCashLog extends BaseIdAndTime {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserEventType eventType;
    @Column(nullable = false)
    private String relTypeCode;
    @Column(nullable = false)
    private Long relId;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CashUser user;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_wallet_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserWallet wallet;
    @Column(precision = 10, scale = 3, nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;
    @Column(precision = 10, scale = 3, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Builder
    public UserCashLog(BigDecimal amount, BigDecimal balance, UserEventType eventType, Long relId,
        String relTypeCode, CashUser user, UserWallet wallet) {
        this.amount = amount;
        this.balance = balance;
        this.eventType = eventType;
        this.relId = relId;
        this.relTypeCode = relTypeCode;
        this.user = user;
        this.wallet = wallet;
    }
}
