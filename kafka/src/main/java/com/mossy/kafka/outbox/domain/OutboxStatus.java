package com.mossy.kafka.outbox.domain;

public enum OutboxStatus {
    PENDING,     // 발행 대기
    PROCESSING,  // 발행 중 (선점)
    PUBLISHED,   // 발행 완료
    FAILED       // 발행 실패 (maxRetry 초과)
}
