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

    @Modifying
    @Query("DELETE FROM OutboxEvent o WHERE o.status = :status AND o.createdAt < :cutoff")
    long deleteByStatusAndCreatedAtBefore(@Param("status") OutboxStatus status, @Param("cutoff") LocalDateTime cutoff);

    /**
     * 원자적 상태 업데이트 (Race Condition 방지)
     * <p>
     * UPDATE outbox_event SET status = :newStatus WHERE id = :id AND status = :currentStatus
     * <p>
     * - 영향받은 행 수 0: 이미 다른 Pod가 처리 중 (획득 실패)
     * - 영향받은 행 수 1: 상태 변경 성공 (획득 성공)
     */
    @Modifying
    @Query("UPDATE OutboxEvent o SET o.status = :newStatus WHERE o.id = :id AND o.status = :currentStatus")
    int updateStatus(@Param("id") Long id,
                     @Param("currentStatus") OutboxStatus currentStatus,
                     @Param("newStatus") OutboxStatus newStatus);
}
