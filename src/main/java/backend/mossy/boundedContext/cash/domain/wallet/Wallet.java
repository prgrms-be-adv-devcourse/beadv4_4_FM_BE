package backend.mossy.boundedContext.cash.domain.wallet;

import backend.mossy.boundedContext.cash.domain.history.CashLog;
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
@Table(name = "CASH_WALLET")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id", column = @Column(name = "wallet_id"))
public class Wallet extends BaseManualIdAndTime {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CashUser user;

    @Column(precision = 10, scale = 3, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "wallet", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<CashLog> cashLogs = new ArrayList<>();

    public Wallet(CashUser user) {
        super(user.getId());
        this.user = user; // 필드명 변경에 따른 할당 수정
        this.balance = BigDecimal.ZERO; // 초기값 설정 변경
    }
}
