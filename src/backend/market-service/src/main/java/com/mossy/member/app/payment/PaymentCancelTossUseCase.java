package com.mossy.member.app.payment;

import com.mossy.member.domain.order.Order;
import com.mossy.member.domain.payment.Payment;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.market.dto.toss.PaymentCancelTossRequestDto;
import com.mossy.shared.market.enums.PayMethod;
import com.mossy.shared.market.event.OrderCancelEvent;
import com.mossy.shared.market.event.PaymentRefundEvent;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentCancelTossUseCase {

    private final PaymentSupport paymentSupport;
    private final EventPublisher eventPublisher;

    @Transactional
    public void orderCancelTossPayment(OrderCancelEvent event) {
        String orderNo = event.orderNo();
        Payment payment = paymentSupport.findPayment(orderNo);
        String paymentKey = payment.getPaymentKey();
        BigDecimal cancelAmount = payment.getAmount();
        String cancelReason = event.cancelReason();

        Order order = paymentSupport.findOrderForCancel(orderNo);

        eventPublisher.publish(new PaymentRefundEvent(
            order.getId(),
            order.getBuyer().getId(),
            cancelAmount,
            PayMethod.CARD
        ));

        paymentSupport.requestTossCancel(paymentKey, cancelReason);

        paymentSupport.processCancel(orderNo, paymentKey, cancelAmount, PayMethod.CARD,
            cancelReason);
    }

    @Transactional
    public void cancelTossPayment(PaymentCancelTossRequestDto request) {
        String orderNo = request.orderId();
        String paymentKey = request.paymentKey();
        BigDecimal cancelAmount = request.cancelAmount();
        String cancelReason = request.cancelReason();

        paymentSupport.findOrderForCancel(orderNo);
        paymentSupport.requestTossCancel(paymentKey, cancelReason);
        paymentSupport.processCancel(orderNo, paymentKey, cancelAmount, PayMethod.CARD,
            cancelReason);
    }
}
