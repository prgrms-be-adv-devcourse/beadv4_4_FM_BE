package com.mossy.infra.scheduler;

import com.mossy.infra.outbox.OutboxEventPublisher;
import com.mossy.kafka.outbox.domain.OutboxEvent;
import com.mossy.kafka.outbox.domain.OutboxStatus;
import com.mossy.kafka.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    // 폴링 스케쥴러 - 실패한 이벤트만 재발행 (30초 이상 지난 PENDING)
    @Scheduled(fixedDelayString = "${outbox.poller.interval-ms}")
    public void pollAndPublish() {
        // 생성된 지 30초 이상 지났는데 아직 PENDING인 것만 조회 (실패한 것)
        LocalDateTime thirtySecondsAgo = LocalDateTime.now().minusSeconds(30);
        List<OutboxEvent> stuckEvents = outboxEventRepository
            .findByStatusAndCreatedAtBeforeOrderByCreatedAtAsc(
                OutboxStatus.PENDING,
                thirtySecondsAgo,
                PageRequest.of(0, batchSize)
            );

        stuckEvents.forEach(event -> {
            boolean acquired = outboxEventPublisher.markProcessing(event.getId());
            if (acquired) {
                outboxEventPublisher.publishAndComplete(event.getId(), maxRetry);
            }
        });
    }

    // 매일 새벽 3시 - 7일 이상 지난 PUBLISHED 이벤트 정리
    @Transactional
    @Scheduled(cron = "${outbox.cleanup.cron}")
    public void cleanupOldEvents() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        outboxEventRepository.deleteByStatusAndCreatedAtBefore(OutboxStatus.PUBLISHED, cutoff);
    }
}
