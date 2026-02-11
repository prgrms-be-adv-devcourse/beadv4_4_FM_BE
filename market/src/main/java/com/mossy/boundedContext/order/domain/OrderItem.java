package com.mossy.boundedContext.order.domain;

import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.market.enums.OrderState;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "MARKET_ORDER_ITEM")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AttributeOverride(name = "id", column = @Column(name = "order_item_id"))
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class OrderItem extends BaseIdAndTime {
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Order order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seller_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MarketSeller seller;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "user_coupon_id")
    private Long userCouponId;

    private int quantity;

    @Column(name = "order_price", precision = 18, scale = 2, nullable = false)
    private BigDecimal originalPrice;;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;

    @Column(name="cancel_reason")
    private String cancelReason;

    @Column(name = "discount_amount", precision = 18, scale = 2)
    private BigDecimal discountAmount;

    static OrderItem create(
            Order order,
            MarketSeller seller,
            Long productId,
            int quantity,
            BigDecimal originalPrice
    ) {
        return OrderItem.builder()
                .order(order)
                .seller(seller)
                .productId(productId)
                .quantity(quantity)
                .originalPrice(originalPrice)
                .state(OrderState.PENDING)
                .build();
    }
}
