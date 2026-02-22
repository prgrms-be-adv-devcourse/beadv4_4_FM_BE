package com.mossy.kafka.publisher;

import com.mossy.kafka.KafkaTopics;
import com.mossy.shared.cash.event.PaymentCashRefundEvent;
import com.mossy.shared.market.event.OrderCancelEvent;
import com.mossy.shared.market.event.OrderStockReturnEvent;
import com.mossy.shared.market.event.OrderPurchaseConfirmedEvent;
import com.mossy.shared.member.event.SellerJoinedEvent;
import com.mossy.shared.member.event.UserJoinedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(Object event) {
        String topic = resolveTopicName(event);

        if (topic == null) {
            log.warn("Kafka 토픽 매핑 없음: eventType={}", event.getClass().getSimpleName());
            return;
        }

        try {
            kafkaTemplate.send(topic, event);
            log.info("Kafka 이벤트 발행 성공: topic={}, eventType={}", topic, event.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Kafka 이벤트 발행 실패: topic={}, eventType={}, error={}", topic, event.getClass().getSimpleName(), e.getMessage());
        }
    }

    private String resolveTopicName(Object event) {
        return switch (event) {
            case PaymentCashRefundEvent e -> KafkaTopics.PAYMENT_REFUND;
            case OrderCancelEvent e -> KafkaTopics.ORDER_CANCEL;
            case OrderStockReturnEvent e -> KafkaTopics.ORDER_STOCK_RETURN;
            case OrderPurchaseConfirmedEvent e -> KafkaTopics.ORDER_PURCHASE_CONFIRMED;
            case UserJoinedEvent e -> KafkaTopics.USER_JOINED;
            case SellerJoinedEvent e -> KafkaTopics.SELLER_JOINED;
            default -> null;
        };
    }
}
