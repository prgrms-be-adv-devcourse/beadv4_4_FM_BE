package com.mossy.boundedContext.cash.in;

import com.mossy.kafka.outbox.domain.OutboxEvent;
import com.mossy.kafka.outbox.domain.OutboxStatus;
import com.mossy.kafka.outbox.repository.OutboxEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class OutboxRelayScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> stringKafkaTemplate;

    public OutboxRelayScheduler(
        OutboxEventRepository outboxEventRepository,
        @Qualifier("stringKafkaTemplate") KafkaTemplate<String, String> stringKafkaTemplate
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.stringKafkaTemplate = stringKafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void relay() {
        List<OutboxEvent> pendingEvents =
            outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING, PageRequest.of(0, 100));

        if (pendingEvents.isEmpty()) {
            return;
        }

        for (OutboxEvent event : pendingEvents) {
            try {
                stringKafkaTemplate.send(
                    event.getTopic(),
                    event.getEventType(),
                    event.getPayload()
                );
                event.markAsPublished();
                log.info("[Outbox Relay] 발행 완료 - topic: {}, key: {}", event.getTopic(), event.getEventType());
            } catch (Exception e) {
                event.markAsFailed(e.getMessage());
                log.error("[Outbox Relay] 발행 실패 - topic: {}, error: {}", event.getTopic(), e.getMessage());
            }
        }
    }
}
