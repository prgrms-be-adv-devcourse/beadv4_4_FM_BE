package com.mossy.kafka.outbox.repository;

import com.mossy.kafka.outbox.domain.OutboxEvent;
import com.mossy.kafka.outbox.domain.OutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status, Pageable pageable);

    List<OutboxEvent> findByStatusAndCreatedAtBeforeOrderByCreatedAtAsc(
        OutboxStatus status, LocalDateTime createdBefore, Pageable pageable);

    void deleteByStatusAndCreatedAtBefore(OutboxStatus status, LocalDateTime cutoff);
}
