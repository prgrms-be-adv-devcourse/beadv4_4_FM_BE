package com.mossy.boundedContext.payment.in;

import com.mossy.boundedContext.payment.app.PaymentFacade;
import com.mossy.shared.market.event.OrderCancelEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaEventListener {

    private final PaymentFacade paymentFacade;

    @KafkaListener(topics = "${app.kafka.topics.order.cancel:order.cancel}")
    public void handleOrderCancelEvent(OrderCancelEvent event) {
        paymentFacade.orderCancelPayment(event);
    }
}
