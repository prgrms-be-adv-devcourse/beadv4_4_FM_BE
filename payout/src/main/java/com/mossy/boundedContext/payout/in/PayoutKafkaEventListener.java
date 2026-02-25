package com.mossy.boundedContext.payout.in;

import com.mossy.boundedContext.payout.app.PayoutFacade;
import com.mossy.kafka.KafkaTopics;
import com.mossy.shared.market.event.OrderPurchaseConfirmedEvent;
import com.mossy.shared.market.event.OrderRefundedEvent;
import com.mossy.shared.member.event.SellerJoinedEvent;
import com.mossy.shared.member.event.SellerUpdatedEvent;
import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.shared.member.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutKafkaEventListener {

    private final PayoutFacade payoutFacade;

    @KafkaListener(topics = KafkaTopics.USER_JOINED)
    public void handleUserJoinedEvent(UserJoinedEvent event) {
        payoutFacade.syncUser(event.user());
    }

    @KafkaListener(topics = KafkaTopics.USER_UPDATED)
    public void handleUserUpdatedEvent(UserUpdatedEvent event) {
        payoutFacade.syncUser(event.user());
    }

    @KafkaListener(topics = KafkaTopics.SELLER_JOINED)
    public void handleSellerJoinedEvent(SellerJoinedEvent event) {
        payoutFacade.syncSeller(event.seller());
    }

    @KafkaListener(topics = KafkaTopics.SELLER_UPDATED)
    public void handleSellerUpdatedEvent(SellerUpdatedEvent event) {
        payoutFacade.syncSeller(event.seller());
    }

    @KafkaListener(topics = KafkaTopics.ORDER_PURCHASE_CONFIRMED)
    public void handleOrderPaidEvent(OrderPurchaseConfirmedEvent event) {
        log.info("[Payout Kafka] 주문 결제 이벤트 수신 - orderId: {}", event.orderId());
        payoutFacade.handleOrderPurchaseConfirmed(event);
    }

    @KafkaListener(topics = KafkaTopics.ORDER_REFUNDED)
    public void handleOrderRefundedEvent(OrderRefundedEvent event) {
        log.info("[Payout Kafka] 주문 환불 이벤트 수신 - orderId: {}", event.orderId());
        payoutFacade.handleOrderRefunded(event);
    }
}
