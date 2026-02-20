package com.mossy.infra.outbox;

import com.mossy.kafka.outbox.domain.OutboxEvent;
import com.mossy.kafka.outbox.domain.OutboxStatus;
import com.mossy.kafka.outbox.repository.OutboxEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxEventPublisher(
        OutboxEventRepository outboxEventRepository,
        @Qualifier("outboxKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // PROCESSING으로 바꾼 후 커밋을 하면 다른 Pod가 중복 처리 못하도록 할 수 있다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean markProcessing(Long eventId) {
        OutboxEvent event = outboxEventRepository.findById(eventId).orElseThrow();

        if (event.getStatus() != OutboxStatus.PENDING) {
            return false; // 다른 Pod가 이미 선점했음
        }

        event.markAsProcessing();
        return true;
    }

    // Kafka 발행 후 결과 커밋
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishAndComplete(Long eventId, int maxRetry) {
        OutboxEvent event = outboxEventRepository.findById(eventId).orElseThrow();

        try {
            kafkaTemplate.send(event.getTopic(), event.getPayload()).get(3, TimeUnit.SECONDS);
            event.markAsPublished();
        } catch (Exception e) {
            if (event.isMaxRetryExceeded(maxRetry)) {
                event.markAsFailed(e.getMessage());  // 수동 처리 필요
            } else {
                event.markAsRetry(e.getMessage());   // PENDING으로 복귀 → 다음 폴링에서 재시도
            }
        }
    }
}
