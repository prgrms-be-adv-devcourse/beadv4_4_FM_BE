package com.mossy.boundedContext.cart.in;

import com.mossy.boundedContext.cart.app.CartFacade;
import com.mossy.kafka.KafkaTopics;
import com.mossy.shared.market.event.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartKafkaEventListener {

    private final CartFacade cartFacade;

    @KafkaListener(topics = KafkaTopics.ORDER_PAID)
    public void handleOrderPaidEvent(OrderPaidEvent event) {
        cartFacade.clearCart(event.buyerId());
    }
}
