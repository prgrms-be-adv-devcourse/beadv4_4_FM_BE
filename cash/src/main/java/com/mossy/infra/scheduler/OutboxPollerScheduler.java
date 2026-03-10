package com.mossy.infra.scheduler;

import com.mossy.infra.outbox.OutboxEventPublisher;
import com.mossy.kafka.outbox.domain.OutboxEvent;
import com.mossy.kafka.outbox.domain.OutboxStatus;
import com.mossy.kafka.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "outbox.poller.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxPollerScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Value("${outbox.poller.batch-size}")
    private int batchSize;

    @Value("${outbox.poller.max-retry}")
    private int maxRetry;

    @Scheduled(fixedDelayString = "${outbox.poller.interval-ms}")
    public void pollAndPublish() {
        LocalDateTime thirtySecondsAgo = LocalDateTime.now().minusSeconds(30);
        List<OutboxEvent> stuckEvents = outboxEventRepository
            .findByStatusAndCreatedAtBeforeOrderByCreatedAtAsc(
                OutboxStatus.PENDING,
                thirtySecondsAgo,
                PageRequest.of(0, batchSize)
            );

        if (stuckEvents.isEmpty()) {
            log.debug("재발행할 Outbox 이벤트 없음 (정상)");
            return;
        }

        log.info("Outbox 실패 이벤트 감지: {} 건. 재발행을 시도합니다.", stuckEvents.size());

        int successCount = 0;
        int failureCount = 0;

        for (OutboxEvent event : stuckEvents) {
            try {
                boolean acquired = outboxEventPublisher.tryAcquire(event.getId());

                if (!acquired) {
                    log.debug("다른 Pod가 이미 처리 중입니다. outboxId={}", event.getId());
                    continue;
                }

                outboxEventPublisher.publishToKafka(event.getId(), maxRetry);
                successCount++;

                log.info("Outbox 이벤트 재발행 성공. outboxId={}, topic={}",
                    event.getId(), event.getTopic());

            } catch (Exception e) {
                failureCount++;
                log.error("Outbox 이벤트 재발행 실패. outboxId={}, topic={}, error={}",
                    event.getId(), event.getTopic(), e.getMessage(), e);
            }
        }

        if (failureCount > 0) {
            log.info("Outbox 재발행 결과: 성공={}, 실패={}", successCount, failureCount);
        }
    }

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
