package backend.mossy.boundedContext.market.domain.order;

import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "MARKET_ORDER_DETAIL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id", column = @Column(name = "order_detail_id"))
public class OrderDetail extends BaseIdAndTime {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Order order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seller_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MarketSeller seller;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "weight_grade_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private WeightGrade weightGrade;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "order_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal orderPrice;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;

    @Builder
    OrderDetail(Order order,
                MarketSeller seller,
                WeightGrade weightGrade,
                Long productId,
                int quantity,
                BigDecimal orderPrice,
                String address,
                OrderState state) {
        this.order = order;
        this.seller = seller;
        this.weightGrade = weightGrade;
        this.productId = productId;
        this.quantity = quantity;
        this.orderPrice = orderPrice;
        this.address = address;
        this.state = state;
    }
}
