package backend.mossy.boundedContext.market.domain.order;

import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import backend.mossy.boundedContext.market.domain.payment.Payment;
import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.boundedContext.market.domain.market.MarketUser;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.*;
import java.math.BigDecimal;
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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @Column(name = "order_no", nullable = false, unique = true)
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
            DeliveryDistance deliveryDistance,
            WeightGrade weightGrade
    ) {
        BigDecimal orderPrice = price.multiply(BigDecimal.valueOf(quantity));
        this.orderDetails.add(
                OrderDetail.create(
                        this,
                        seller,
                        productId,
                        quantity,
                        orderPrice,
                        deliveryDistance,
                        weightGrade
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

    public LocalDateTime createTossPayment(String paymentKey, String orderNo, BigDecimal amount, PayMethod method) {
        validateNotPaid();
        validateAmount(amount);

        Payment newPayment = Payment.createTossPaid(this, paymentKey, orderNo, amount, method);
        this.payments.add(newPayment);
        // this.state = OrderState.PAID; (결제완료 이벤트 수신하고 처리)

        return newPayment.getCreatedAt();
    }

    public LocalDateTime createCashPayment(BigDecimal amount, PayMethod method) {
        validateNotPaid();
        validateAmount(amount);

        Payment newPayment = Payment.createCashPaid(this, amount, method);
        this.payments.add(newPayment);
        // this.state = OrderState.PAID; (결제완료 이벤트 수신하고 처리)

        return newPayment.getCreatedAt();
    }

    public void failPayment(String paymentKey, BigDecimal amount, PayMethod method, String failReason) {
        Payment failedPayment = Payment.createFailed(this, paymentKey, amount, method, failReason);
        this.payments.add(failedPayment);
    }

    public void cancelPayment(String paymentKey, BigDecimal amount, PayMethod method, String cancelReason) {
        Payment canceledPayment = Payment.createCanceled(this, paymentKey, amount, method, cancelReason);
        this.payments.add(canceledPayment);
    }
}
