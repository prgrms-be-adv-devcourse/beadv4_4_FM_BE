package com.mossy.boundedContext.order.domain;

import com.mossy.boundedContext.coupon.domain.UserCoupon;
import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.market.enums.CouponType;
import com.mossy.shared.market.enums.IssuerType;
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
    @JoinColumn(name = "user_coupon_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserCoupon userCoupon;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "product_item_id")
    private Long productItemId;

    @Column(nullable = false)
    private int quantity;

    @Column(precision = 10, scale = 3, nullable = false)
    private BigDecimal weight;

    @Column(name = "order_price", precision = 18, scale = 2, nullable = false)
    private BigDecimal originalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;

    @Column(name="cancel_reason")
    private String cancelReason;

    @Column(name = "discount_amount", precision = 18, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "final_price", precision = 18, scale = 2, nullable = false)
    private BigDecimal finalPrice;

    static OrderItem create(
            Order order,
            Long sellerId,
            Long productItemId,
            int quantity,
            BigDecimal weight,
            UserCoupon userCoupon,
            BigDecimal originalPrice,
            BigDecimal discountAmount
    ) {
        BigDecimal finalPrice = discountAmount != null
                ? originalPrice.subtract(discountAmount)
                : originalPrice;

        return OrderItem.builder()
                .order(order)
                .sellerId(sellerId)
                .productItemId(productItemId)
                .quantity(quantity)
                .weight(weight)
                .userCoupon(userCoupon)
                .originalPrice(originalPrice)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .state(OrderState.PENDING)
                .build();
    }

    public void updateState(OrderState newState) {
        this.state = newState;
    }

    public void updateCancelReason(String reason) {
        this.cancelReason = reason;
    }

    public Long getUserCouponId() {
        return userCoupon != null ? userCoupon.getId() : null;
    }

    public CouponType getCouponType() {
        return userCoupon != null ? userCoupon.getCoupon().getCouponType() : null;
    }

    public IssuerType getIssuerType() {
        return userCoupon != null ? userCoupon.getCoupon().getIssuerType() : null;
    }
}
