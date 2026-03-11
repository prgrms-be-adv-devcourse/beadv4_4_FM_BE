package com.mossy.kafka.outbox.repository;

import com.mossy.kafka.outbox.domain.OutboxEvent;
import com.mossy.kafka.outbox.domain.OutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status, Pageable pageable);

    List<OutboxEvent> findByStatusAndCreatedAtBeforeOrderByCreatedAtAsc(
        OutboxStatus status, LocalDateTime createdBefore, Pageable pageable);

    List<OutboxEvent> findByStatusAndUpdatedAtBeforeOrderByCreatedAtAsc(
        OutboxStatus status, LocalDateTime updatedBefore, Pageable pageable);

    @Modifying
    @Query("DELETE FROM OutboxEvent o WHERE o.status = :status AND o.createdAt < :cutoff")
    long deleteByStatusAndCreatedAtBefore(@Param("status") OutboxStatus status, @Param("cutoff") LocalDateTime cutoff);

    @Modifying
    @Query("UPDATE OutboxEvent o SET o.status = :newStatus WHERE o.id = :id AND o.status = :currentStatus")
    int updateStatus(@Param("id") Long id,
                     @Param("currentStatus") OutboxStatus currentStatus,
                     @Param("newStatus") OutboxStatus newStatus);
}
