package backend.mossy.boundedContext.cash.domain.seller;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

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
        this.seller = seller;
        this.balance = BigDecimal.ZERO;
    }
}