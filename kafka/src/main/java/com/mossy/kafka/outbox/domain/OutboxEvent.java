package com.mossy.kafka.outbox.domain;

import com.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "OUTBOX_EVENT", indexes = {
        @Index(name = "idx_outbox_status_created", columnList = "status, createdAt")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent extends BaseIdAndTime {

    @Column(nullable = false, length = 200)
    private String topic;

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
    public OutboxEvent(String topic, String eventKey, String payload) {
        this.topic = topic;
        this.eventType = eventKey;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.retryCount = 0;
    }

    public void markAsPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = OutboxStatus.FAILED;
        this.errorMessage = errorMessage;
        this.retryCount++;
    }
}
