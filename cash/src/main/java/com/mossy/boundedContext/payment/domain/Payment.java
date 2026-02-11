package com.mossy.boundedContext.payment.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import com.mossy.shared.cash.enums.PayMethod;
import com.mossy.shared.cash.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PAYMENT_PAYMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "payment_id"))
public class Payment extends BaseIdAndTime {
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "payment_key")
    private String paymentKey; // PG사 거래 고유 번호

    @Column(name = "order_no", nullable = false)
    private String orderNo; // 비즈니스 주문번호

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "fail_reason")
    private String failReason;  //취소 사유

    @Builder
    private Payment(Long orderId, String paymentKey, String orderNo, BigDecimal amount,
                    PayMethod payMethod, PaymentStatus status, String failReason) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.orderNo = orderNo;
        this.amount = amount;
        this.payMethod = payMethod;
        this.status = status != null ? status : PaymentStatus.PAID;
        this.failReason = failReason;
    }

    // 결제 성공 시 호출
    public static Payment createTossPaid(Long orderId, String paymentKey, String orderNo, BigDecimal amount, PayMethod payMethod) {
        return Payment.builder()
            .orderId(orderId)
            .paymentKey(paymentKey)
            .orderNo(orderNo) // ORDER_UUID+난수 승인된 orderNo으로 매칭
            .amount(amount)
            .payMethod(payMethod)
            .status(PaymentStatus.PAID)
            .build();
    }

    // 결제 성공 시 호출
    public static Payment createCashPaid(Long orderId, String orderNo, BigDecimal amount, PayMethod payMethod) {
        return Payment.builder()
            .orderId(orderId)
            .orderNo(orderNo)
            .amount(amount)
            .payMethod(payMethod)
            .status(PaymentStatus.PAID)
            .build();
    }

    // 결제 실패 시 호출
    public static Payment createFailed(Long orderId, String paymentKey, String orderNo, BigDecimal amount,
                                        PayMethod payMethod, String failReason) {
        return Payment.builder()
            .orderId(orderId)
            .paymentKey(paymentKey)
            .orderNo(orderNo)
            .amount(amount)
            .payMethod(payMethod)
            .status(PaymentStatus.FAILED)
            .failReason(failReason)
            .build();
    }

    // 결제 취소 시 호출
    public static Payment createCanceled(Long orderId, String paymentKey, String orderNo, BigDecimal amount,
                                          PayMethod payMethod, String cancelReason) {
        return Payment.builder()
            .orderId(orderId)
            .paymentKey(paymentKey)
            .orderNo(orderNo)
            .amount(amount)
            .payMethod(payMethod)
            .status(PaymentStatus.CANCELED)
            .failReason(cancelReason)
            .build();
    }
}
