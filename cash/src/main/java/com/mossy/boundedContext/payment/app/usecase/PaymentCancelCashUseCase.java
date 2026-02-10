package com.mossy.boundedContext.payment.app.usecase;

import com.mossy.boundedContext.payment.app.PaymentSupport;
import com.mossy.boundedContext.payment.domain.Payment;
import com.mossy.boundedContext.payment.in.dto.request.PaymentCancelCashRequestDto;
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
public class PaymentCancelCashUseCase {

    private final PaymentSupport paymentSupport;
    private final EventPublisher eventPublisher;

    @Transactional
    public void orderCancelCashPayment(OrderCancelEvent event) {
        String orderNo = event.orderNo();
        Payment payment = paymentSupport.findPayment(orderNo);
        BigDecimal cancelAmount = payment.getAmount();
        String cancelReason = event.cancelReason();

        MarketOrderResponse order = paymentSupport.findOrderForCancel(payment.getOrderId());

        eventPublisher.publish(new PaymentRefundEvent(
            order.orderId(),
            order.buyerId(),
            cancelAmount,
            PayMethod.CASH
        ));

        paymentSupport.processCancel(orderNo, null, cancelAmount, PayMethod.CASH, cancelReason);
    }

    @Transactional
    public void cancelCashPayment(PaymentCancelCashRequestDto request) {
        String orderNo = request.orderId();
        BigDecimal cancelAmount = request.cancelAmount();
        String cancelReason = request.cancelReason();

        Payment payment = paymentSupport.findPayment(orderNo);
        paymentSupport.findOrderForCancel(payment.getOrderId());
        paymentSupport.processCancel(orderNo, null, cancelAmount, PayMethod.CASH, cancelReason);
    }
}
