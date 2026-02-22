package com.mossy.boundedContext.payout.in;

import com.mossy.boundedContext.payout.app.PayoutFacade;
import com.mossy.shared.market.event.OrderPurchaseConfirmedEvent;
import com.mossy.shared.market.event.OrderRefundedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutKafkaEventListener {

    private final PayoutFacade payoutFacade;

    @KafkaListener(topics = "${app.kafka.topics.order.PurchaseConfirmed}")
    public void handleOrderPaidEvent(OrderPurchaseConfirmedEvent event) {
        log.info("[Payout Kafka] 주문 결제 이벤트 수신 - orderId: {}", event.orderId());
        payoutFacade.handleOrderPurchaseConfirmed(event);
    }

    @KafkaListener(topics = "${app.kafka.topics.order.refunded}")
    public void handleOrderRefundedEvent(OrderRefundedEvent event) {
        log.info("[Payout Kafka] 주문 환불 이벤트 수신 - orderId: {}", event.orderId());
        payoutFacade.handleOrderRefunded(event);
    }
}
