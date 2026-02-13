package com.mossy.kafka.publisher;

import com.mossy.kafka.KafkaTopics;
import com.mossy.shared.cash.event.PaymentRefundEvent;
import com.mossy.shared.market.event.OrderCancelEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(Object event) {
        String topic = resolveTopicName(event);

        if (topic == null) {
            return;
        }

        kafkaTemplate.send(topic, event);
    }

    private String resolveTopicName(Object event) {
        return switch (event) {
            case PaymentRefundEvent e -> KafkaTopics.PAYMENT_REFUND;
            case OrderCancelEvent e -> KafkaTopics.ORDER_CANCEL;
            default -> null;
        };
    }
}
