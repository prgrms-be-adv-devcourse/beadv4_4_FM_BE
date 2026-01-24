package backend.mossy.boundedContext.market.domain.order;

import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "MARKET_ORDER_DETAIL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id", column = @Column(name = "order_detail_id"))
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class OrderDetail extends BaseIdAndTime {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Order order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seller_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MarketSeller seller;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "weight_grade_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private WeightGrade weightGrade;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "delivery_distance_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private DeliveryDistance deliveryDistance;

    @Column(name = "product_id")
    private Long productId;

    private int quantity;

    @Column(name = "order_price", precision = 10, scale = 2)
    private BigDecimal orderPrice;

    static OrderDetail create(
            Order order,
            MarketSeller seller,
            Long productId,
            int quantity,
            BigDecimal orderPrice,
            DeliveryDistance deliveryDistance,
            WeightGrade weightGrade
    ) {
        return OrderDetail.builder()
                .order(order)
                .seller(seller)
                .productId(productId)
                .quantity(quantity)
                .orderPrice(orderPrice)
                .deliveryDistance(deliveryDistance)
                .weightGrade(weightGrade)
                .build();
    }
}
