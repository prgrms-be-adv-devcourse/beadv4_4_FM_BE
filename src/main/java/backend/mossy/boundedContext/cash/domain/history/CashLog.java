package backend.mossy.boundedContext.cash.domain.history;

import static jakarta.persistence.FetchType.LAZY;

import backend.mossy.boundedContext.cash.domain.wallet.CashUser;
import backend.mossy.boundedContext.cash.domain.wallet.Wallet;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CASH_CASH_LOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CashLog extends BaseIdAndTime {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;
    @Column(nullable = false)
    private String relTypeCode;
    @Column(nullable = false)
    private int relId;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private CashUser member;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
    @Column(nullable = false)
    private long amount;
    @Column(nullable = false)
    private long balance;
}
