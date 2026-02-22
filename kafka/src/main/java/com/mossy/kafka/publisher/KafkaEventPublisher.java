package com.mossy.kafka.publisher;

import com.mossy.kafka.KafkaTopics;
import com.mossy.shared.cash.event.PaymentCashRefundEvent;
import com.mossy.shared.market.event.CouponUseRequestedEvent;
import com.mossy.shared.market.event.OrderCancelEvent;
import com.mossy.shared.market.event.OrderPurchaseConfirmedEvent;
import com.mossy.shared.member.event.SellerJoinedEvent;
import com.mossy.shared.member.event.UserJoinedEvent;
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
            case PaymentCashRefundEvent e -> KafkaTopics.PAYMENT_REFUND;
            case OrderCancelEvent e -> KafkaTopics.ORDER_CANCEL;
            case OrderPurchaseConfirmedEvent e -> KafkaTopics.ORDER_PURCHASE_CONFIRMED;
            case CouponUseRequestedEvent e -> KafkaTopics.COUPON_USE_REQUESTED;
            case UserJoinedEvent e -> KafkaTopics.USER_JOINED;
            case SellerJoinedEvent e -> KafkaTopics.SELLER_JOINED;
            default -> null;
        };
    }
}
