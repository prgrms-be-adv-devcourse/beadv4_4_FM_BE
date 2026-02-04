package com.mossy.member.domain.user;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.cash.enums.UserEventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "CASH_USER_LOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "cash_log_id"))
public class UserCashLog extends BaseIdAndTime {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserEventType eventType;
    private String relTypeCode;
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
