package backend.mossy.boundedContext.market.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "MARKET_ORDER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseIdAndTime {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private MarketUser buyer;

    @Column(name = "order_no", nullable = false)
    private String orderNo;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;
}
