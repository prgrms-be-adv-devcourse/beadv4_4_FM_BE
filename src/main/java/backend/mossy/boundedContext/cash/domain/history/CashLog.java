package backend.mossy.boundedContext.cash.domain.history;

import static jakarta.persistence.FetchType.LAZY;

import backend.mossy.boundedContext.cash.domain.wallet.CashMember;
import backend.mossy.boundedContext.cash.domain.wallet.Wallet;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CASH_CASH_LOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "cash_log_id"))
public class CashLog extends BaseIdAndTime {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;
    @Column(nullable = false)
    private String relTypeCode;
    @Column(nullable = false)
    private int relId;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CashMember member;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "wallet_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Wallet wallet;
    @Column(nullable = false)
    private long amount;
    @Column(nullable = false)
    private long balance;
}
