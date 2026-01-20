package backend.mossy.boundedContext.market.domain.order;

import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.boundedContext.market.domain.market.MarketUser;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import backend.mossy.shared.market.dto.event.OrderDetailDto;
import backend.mossy.shared.market.dto.event.PaymentOrderDto;
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

    @Column(name = "order_no", nullable = false)
    private String orderNo;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public static Order create(MarketUser buyer, PaymentOrderDto paymentOrderDto) {
        return Order.builder()
                .buyer(buyer)
                .orderNo(paymentOrderDto.orderNo())
                .totalPrice(paymentOrderDto.totalPrice())
                .state(OrderState.PAID)
                .build();
    }

    public void addOrderDetail(MarketSeller seller, WeightGrade weightGrade, OrderDetailDto dto) {
        OrderDetail detail = OrderDetail.builder()
                .order(this)
                .seller(seller)
                .weightGrade(weightGrade)
                .productId(dto.productId())
                .quantity(dto.quantity())
                .orderPrice(dto.orderPrice())
                .address(dto.address())
                .state(OrderState.PAID)
                .build();

        this.orderDetails.add(detail);
    }
}
