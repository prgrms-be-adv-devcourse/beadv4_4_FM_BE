package com.mossy.boundedContext.out.kafka;

import com.mossy.kafka.publisher.KafkaEventPublisher;
import com.mossy.shared.member.event.UserJoinedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserKafkaEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;

    // 회원가입 완료 후 Kafka로 user.joined 이벤트 발행
    // → market(장바구니 생성), cash(지갑 생성), payout(정산 유저 생성) 에서 수신
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void publishUserJoined(UserJoinedEvent event) {
        log.info("Kafka 이벤트 발행: user.joined, userId={}", event.user().id());
        kafkaEventPublisher.publish(event);
    }
}

