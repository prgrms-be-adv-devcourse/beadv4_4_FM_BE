package backend.mossy.boundedContext.market.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "MARKET_ORDER_DETAIL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderDetail extends BaseIdAndTime {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "seller_id")
    private MarketSeller seller;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @ManyToOne(fetch = LAZY)
    private WeightGrade weightGrade;

    @Column(nullable = false)
    private int count;

    @Column(name = "order_price", nullable = false)
    private int orderPrice;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;
}
