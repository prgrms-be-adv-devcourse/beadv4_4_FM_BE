package backend.mossy.boundedContext.cash.domain.seller;

import static jakarta.persistence.FetchType.LAZY;

import backend.mossy.boundedContext.cash.domain.user.CashUser;
import backend.mossy.boundedContext.cash.domain.user.UserEventType;
import backend.mossy.boundedContext.cash.domain.user.UserWallet;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "CASH_SELLER_LOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "seller_log_id"))
public class SellerCashLog extends BaseIdAndTime {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserEventType eventType;
    @Column(nullable = false)
    private String relTypeCode;
    @Column(nullable = false)
    private int relId;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seller_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CashSeller user;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seller_wallet_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private SellerWallet wallet;
    @Column(precision = 10, scale = 3, nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;
    @Column(precision = 10, scale = 3, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
}


