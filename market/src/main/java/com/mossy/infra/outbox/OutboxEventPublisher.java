package com.mossy.infra.outbox;

import com.mossy.kafka.outbox.domain.OutboxEvent;
import com.mossy.kafka.outbox.domain.OutboxStatus;
import com.mossy.kafka.outbox.repository.OutboxEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${outbox.publish.timeout-seconds:10}")
    private int publishTimeoutSeconds;

    public OutboxEventPublisher(
        OutboxEventRepository outboxEventRepository,
        @Qualifier("outboxKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recoverProcessingEvent(Long eventId) {
        int updated = outboxEventRepository.updateStatus(
            eventId,
            OutboxStatus.PROCESSING,
            OutboxStatus.PENDING
        );

        if (updated > 0) {
            log.info("이벤트 복구 완료. outboxId={}", eventId);
        }
    }

    //Native query를 통해서 Race Condition 방지
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean tryAcquire(Long eventId) {
        int updated = outboxEventRepository.updateStatus(
            eventId,
            OutboxStatus.PENDING,
            OutboxStatus.PROCESSING
        );

        if (updated > 0) {
            log.debug("Outbox 이벤트 선점 성공. outboxId={}", eventId);
            return true;
        } else {
            log.debug("이미 처리 중이거나 완료된 이벤트입니다. outboxId={}", eventId);
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishToKafka(Long eventId, int maxRetry) {
        OutboxEvent event = outboxEventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Outbox 이벤트를 찾을 수 없습니다. id=" + eventId));

        try {
            kafkaTemplate.send(event.getTopic(), event.getPayload()).get(publishTimeoutSeconds, TimeUnit.SECONDS);
            event.markAsPublished();
            log.info("Kafka 발행 성공. outboxId={}, topic={}", eventId, event.getTopic());

        } catch (Exception e) {
            if (event.isMaxRetryExceeded(maxRetry)) {
                event.markAsFailed(e.getMessage());
                log.info("Kafka 발행 최대 재시도 초과. FAILED 처리. outboxId={}, topic={}, error={}",
                    eventId, event.getTopic(), e.getMessage());
            } else {
                event.markAsRetry(e.getMessage());
                log.info("Kafka 발행 실패. 재시도 예정. outboxId={}, retryCount={}, error={}",
                    eventId, event.getRetryCount(), e.getMessage());
            }
        }
    }
}
