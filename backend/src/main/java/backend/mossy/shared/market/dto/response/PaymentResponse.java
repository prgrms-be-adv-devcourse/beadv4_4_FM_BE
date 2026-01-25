package backend.mossy.shared.market.dto.response;

import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import backend.mossy.boundedContext.market.domain.payment.Payment;
import backend.mossy.boundedContext.market.domain.payment.PaymentStatus;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentResponse(
    Long paymentId,
    String paymentKey,
    String orderNo,
    BigDecimal amount,
    PayMethod payMethod,
    PaymentStatus status,
    String failReason,
    LocalDateTime createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
            .paymentId(payment.getId())
            .paymentKey(payment.getPaymentKey())
            .orderNo(payment.getOrderNo())
            .amount(payment.getAmount())
            .payMethod(payment.getPayMethod())
            .status(payment.getStatus())
            .failReason(payment.getFailReason())
            .createdAt(payment.getCreatedAt())
            .build();
    }
}