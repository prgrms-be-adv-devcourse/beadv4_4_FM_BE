package com.mossy.boundedContext.order.domain;

import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.boundedContext.product.in.dto.response.ProductInfoResponse;
import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.market.enums.OrderState;
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

    @Column(name = "order_no", nullable = false, unique = true)
    private String orderNo;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;

    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    public static Order create(
            MarketUser buyer,
            String address,
            String orderNo,
            Map<Long, MarketSeller> sellerMap,
            List<ProductInfoResponse> items,
            BigDecimal totalPrice
    ) {
        Order order = Order.builder()
                .buyer(buyer)
                .orderNo(orderNo)
                .address(address)
                .state(OrderState.PENDING)
                .totalPrice(totalPrice)
                .build();

        items.forEach(item -> order.addOrderItem(sellerMap.get(item.sellerId()), item));

        return order;
    }

    private void addOrderItem(MarketSeller seller, ProductInfoResponse item) {
        BigDecimal orderPrice = item.price().multiply(BigDecimal.valueOf(item.quantity()));
        OrderItem orderItem = OrderItem.create(this, seller, item.productId(), item.quantity(), orderPrice);
        this.orderItems.add(orderItem);
    }

    public void completePayment() {
        this.state = OrderState.PAID;
    }

    public void expire() {
        if (this.state == OrderState.PENDING) {
            this.state = OrderState.EXPIRED;
        }
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
