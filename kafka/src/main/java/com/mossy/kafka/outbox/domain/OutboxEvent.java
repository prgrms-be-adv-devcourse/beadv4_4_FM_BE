package com.mossy.kafka.outbox.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "OUTBOX_EVENT",
    indexes = {
        // 폴링 성능용 인덱스
        @Index(name = "idx_outbox_status_created", columnList = "status, createdAt"),
        // 도메인 별 조회용 인덱스
        @Index(name = "uk_outbox_aggregate", columnList = "aggregateType, aggregateId, eventType")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent extends BaseIdAndTime {

    @Column(nullable = false, length = 200)
    private String topic;

    // ex) Order, Cash
    @Column(nullable = false, length = 50)
    private String aggregateType;

    // 중복 방지용 id
    @Column(nullable = false)
    private Long aggregateId;

    // 이벤트 클래스
    @Column(nullable = false, length = 50)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status;

    // 발행 완료 시간
    @Column
    private LocalDateTime publishedAt;

    // 재시도 횟수
    @Column
    private Integer retryCount;

    // 에러 메세지
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Builder
    public OutboxEvent(String topic, String aggregateType, Long aggregateId, String eventKey, String payload) {
        this.topic = topic;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventKey;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.retryCount = 0;
    }

    public void markAsProcessing() {
        this.status = OutboxStatus.PROCESSING;
    }

    public void markAsPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void markAsRetry(String errorMessage) {
        this.status = OutboxStatus.PENDING;
        this.errorMessage = errorMessage;
        this.retryCount++;
    }

    public void markAsFailed(String errorMessage) {
        this.status = OutboxStatus.FAILED;
        this.errorMessage = errorMessage;
        this.retryCount++;
    }

    public boolean isMaxRetryExceeded(int maxRetry) {
        return this.retryCount >= maxRetry;
    }
}
