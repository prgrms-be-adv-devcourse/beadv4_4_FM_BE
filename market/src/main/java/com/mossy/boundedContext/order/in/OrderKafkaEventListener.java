package com.mossy.boundedContext.order.in;

import com.mossy.boundedContext.order.app.OrderFacade;
import com.mossy.kafka.KafkaTopics;
import com.mossy.shared.cash.event.PaymentCompletedEvent;
import com.mossy.shared.cash.event.PaymentTossRefundEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaEventListener {

    private final OrderFacade orderFacade;

    @KafkaListener(topics = KafkaTopics.PAYMENT_COMPLETED)
    public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {
        orderFacade.completePayment(event);
    }

    @KafkaListener(topics = KafkaTopics.ORDER_CANCEL)
    public void handleOrderCanceledEvent(PaymentTossRefundEvent event) {
        orderFacade.cancelOrder(event.response());
    }
}
