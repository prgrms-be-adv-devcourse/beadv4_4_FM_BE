package com.mossy.infra.outbox;

import com.mossy.kafka.outbox.event.OutboxSavedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxImmediatePublisher {

    private final OutboxEventPublisher outboxEventPublisher;

    @Value("${outbox.poller.max-retry}")
    private int maxRetry;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOutboxSaved(OutboxSavedEvent event) {

        boolean acquired = outboxEventPublisher.tryAcquire(event.outboxId());

        if (!acquired) {
            log.debug("다른 Pod가 이미 처리 중인 이벤트입니다. outboxId={}", event.outboxId());
            return;
        }

        try {
            outboxEventPublisher.publishToKafka(event.outboxId(), maxRetry);

            log.debug("Outbox 이벤트 즉시 발행 완료. outboxId={}", event.outboxId());

        } catch (Exception e) {
            log.error("Outbox 이벤트 즉시 발행 실패. Poller가 재시도합니다. outboxId={}, error={}",
                event.outboxId(), e.getMessage());
        }
    }
}
