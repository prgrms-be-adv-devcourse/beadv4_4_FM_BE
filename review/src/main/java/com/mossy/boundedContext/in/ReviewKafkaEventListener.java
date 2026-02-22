package com.mossy.boundedContext.in;

import com.mossy.boundedContext.app.ReviewFacade;
import com.mossy.kafka.KafkaTopics;
import com.mossy.shared.market.event.OrderPurchaseConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewKafkaEventListener {

    private final ReviewFacade reviewFacade;

    @KafkaListener(topics = KafkaTopics.ORDER_PURCHASE_CONFIRMED)
    public void handleOrderPurchaseConfirmed(OrderPurchaseConfirmedEvent event) {
        log.info("[Review Kafka] 주문 확정 이벤트 수신 - orderId: {}", event.orderId());
        reviewFacade.handleOrderPurchaseConfirmed(event);
    }
}
