package com.mossy.boundedContext.payment.app.usecase;

import com.mossy.boundedContext.payment.app.PaymentSupport;
import com.mossy.boundedContext.payment.domain.Payment;
import com.mossy.boundedContext.payment.in.dto.request.PaymentCancelTossRequestDto;
import com.mossy.boundedContext.payment.out.dto.response.MarketOrderResponse;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.cash.enums.PayMethod;
import com.mossy.shared.cash.event.PaymentRefundEvent;
import com.mossy.shared.market.event.OrderCancelEvent;
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

        MarketOrderResponse order = paymentSupport.findOrderForCancel(payment.getOrderId());

        eventPublisher.publish(new PaymentRefundEvent(
            order.orderId(),
            order.buyerId(),
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

        Payment payment = paymentSupport.findPayment(orderNo);
        paymentSupport.findOrderForCancel(payment.getOrderId());
        paymentSupport.requestTossCancel(paymentKey, cancelReason);
        paymentSupport.processCancel(orderNo, paymentKey, cancelAmount, PayMethod.CARD,
            cancelReason);
    }
}
