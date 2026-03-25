package com.mossy.kafka.outbox.service;

import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.kafka.outbox.domain.OutboxEvent;
import com.mossy.kafka.outbox.event.OutboxSavedEvent;
import com.mossy.kafka.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final EventPublisher eventPublisher;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveEvent(String topic, String aggregateType, Long aggregateId, String eventKey, Object event) {
        saveEventInternal(topic, aggregateType, aggregateId, eventKey, event);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCompensationEvent(String topic, String aggregateType, Long aggregateId, String eventKey, Object event) {
        saveEventInternal(topic, aggregateType, aggregateId, eventKey, event);
    }

    private void saveEventInternal(String topic, String aggregateType, Long aggregateId, String eventKey, Object event) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .topic(topic)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventKey(eventKey)
                .payload(event.toString())
                .build();

        // 트랜잭션 커밋 후 즉시 Kafka로 발행하기 위한 Spring Event 발행
        OutboxEvent savedEvent = outboxEventRepository.save(outboxEvent);

        eventPublisher.publish(new OutboxSavedEvent(savedEvent.getId()));
    }
}
