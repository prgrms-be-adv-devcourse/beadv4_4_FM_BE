package com.mossy.infra.scheduler;

import com.mossy.infra.outbox.OutboxEventPublisher;
import com.mossy.kafka.outbox.domain.OutboxEvent;
import com.mossy.kafka.outbox.domain.OutboxStatus;
import com.mossy.kafka.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPollerScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Value("${outbox.poller.batch-size}")
    private int batchSize;

    @Value("${outbox.poller.max-retry}")
    private int maxRetry;

    // 실패한 이벤트 재발행 스케쥴러
    @Scheduled(fixedDelayString = "${outbox.poller.interval-ms}")
    public void pollAndPublish() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(10);

        List<OutboxEvent> stuckEvents = outboxEventRepository
            .findByStatusInAndUpdatedAtBeforeOrderByCreatedAtAsc(
                List.of(OutboxStatus.PENDING, OutboxStatus.PROCESSING),
                threshold,
                PageRequest.of(0, batchSize)
            );

        if (stuckEvents.isEmpty()) {
            log.info("재발행할 Outbox 이벤트 없음");
            return;
        }

        log.info("Outbox 실패 : {} 건. 재발행을 시도합니다.", stuckEvents.size());

        for (OutboxEvent event : stuckEvents) {
            try {
                if (event.getStatus() == OutboxStatus.PROCESSING) {
                    outboxEventPublisher.recoverProcessingEvent(event.getId());
                }

                boolean acquired = outboxEventPublisher.tryAcquire(event.getId());

                if (!acquired) {
                    log.debug("다른 Pod가 이미 처리 중입니다. outboxId={}", event.getId());
                    continue;
                }

                outboxEventPublisher.publishToKafka(event.getId(), maxRetry);

                log.info("Outbox 이벤트 재발행 성공. outboxId={}, topic={}",
                    event.getId(), event.getTopic());

            } catch (Exception e) {
                log.error("Outbox 이벤트 재발행 실패. outboxId={}, topic={}, error={}",
                    event.getId(), event.getTopic(), e.getMessage(), e);
            }
        }
    }

    // PUBLISHED 이벤트 삭제 스케쥴러
    @Transactional
    @Scheduled(cron = "${outbox.cleanup.cron}")
    public void cleanupOldEvents() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);

        long deletedCount = outboxEventRepository
            .deleteByStatusAndCreatedAtBefore(OutboxStatus.PUBLISHED, cutoff);

        if (deletedCount > 0) {
            log.info("Outbox 이벤트 정리 완료: {} 건 삭제", deletedCount);
        }
    }
}
