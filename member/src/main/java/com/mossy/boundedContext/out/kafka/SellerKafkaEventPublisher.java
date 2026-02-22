package com.mossy.boundedContext.out.kafka;

import com.mossy.kafka.publisher.KafkaEventPublisher;
import com.mossy.shared.member.event.SellerJoinedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class SellerKafkaEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;

    // 판매자 승인 완료 후 Kafka로 seller.joined 이벤트 발행
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void publishSellerJoined(SellerJoinedEvent event) {
        log.info("Kafka 이벤트 발행: seller.joined, sellerId={}", event.seller().sellerId());
        kafkaEventPublisher.publish(event);
    }
}

