package backend.mossy.boundedContext.market.domain.order;

import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.boundedContext.market.domain.market.MarketUser;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "MARKET_ORDER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id", column = @Column(name = "order_id"))
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Order extends BaseIdAndTime {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MarketUser buyer;

    @Column(name = "order_no", nullable = false)
    private String orderNo;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public static Order create(
            MarketUser buyer,
            String orderNo
    ) {
        return Order.builder()
                .buyer(buyer)
                .orderNo(orderNo)
                .address(buyer.getAddress())
                .totalPrice(BigDecimal.ZERO)
                .state(OrderState.PENDING)
                .build();
    }

    public void addOrderDetail(
            MarketSeller seller,
            Long productId,
            int quantity,
            BigDecimal price,
            DeliveryDistance deliveryDistance
    ) {
        BigDecimal orderPrice = price.multiply(BigDecimal.valueOf(quantity));
        this.orderDetails.add(
                OrderDetail.create(
                        this,
                        seller,
                        productId,
                        quantity,
                        orderPrice,
                        deliveryDistance
                )
        );
        this.totalPrice = this.totalPrice.add(orderPrice);
    }

    public List<Long> getProductIds() {
        List<Long> productIds = new ArrayList<>();
        for (OrderDetail detail : this.orderDetails) {
            productIds.add(detail.getProductId());
        }
        return productIds;
    }

    public void calculateWeightGrades(Map<Long, BigDecimal> weightMap, List<WeightGrade> grades) {
        for (OrderDetail detail : this.orderDetails) {
            BigDecimal weight = weightMap.get(detail.getProductId());
            detail.calculateWeightGrade(weight, grades);
        }
    }

    public void completePayment() {
        this.state = OrderState.PAID;
    }
}
