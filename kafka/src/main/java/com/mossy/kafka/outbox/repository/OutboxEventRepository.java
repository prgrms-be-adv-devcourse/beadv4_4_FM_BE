package com.mossy.kafka.outbox.repository;

import com.mossy.kafka.outbox.domain.OutboxEvent;
import com.mossy.kafka.outbox.domain.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
