package com.mossy.boundedContext.order.domain;

import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.market.enums.OrderState;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "order_no", nullable = false, unique = true)
    private String orderNo;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

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
            BigDecimal price
    ) {
        BigDecimal orderPrice = price.multiply(BigDecimal.valueOf(quantity));
        this.orderItems.add(
                OrderItem.create(
                        this,
                        seller,
                        productId,
                        quantity,
                        orderPrice
                )
        );
        this.totalPrice = this.totalPrice.add(orderPrice);
    }

    public void completePayment() {
        this.state = OrderState.PAID;
    }

    public void validateAmount(BigDecimal requestAmount) {
        if (this.totalPrice.compareTo(requestAmount) != 0) {
            throw new DomainException(ErrorCode.ORDER_AMOUNT_MISMATCH);
        }
    }

    public void validatePendingState() {
        if (this.state != OrderState.PENDING) {
            throw new DomainException(ErrorCode.INVALID_ORDER_STATE);
        }
    }

    private void validateNotPaid() {
        if (this.state == OrderState.PAID) {
            throw new DomainException(ErrorCode.ORDER_ALREADY_PAID);
        }
    }
}
