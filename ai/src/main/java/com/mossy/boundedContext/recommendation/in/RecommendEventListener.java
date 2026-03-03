package com.mossy.boundedContext.recommendation.in;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import com.mossy.boundedContext.recommendation.app.RecommendFacade;
import com.mossy.boundedContext.recommendation.app.mapper.RecommendMapper;
import com.mossy.exception.DomainException;
import com.mossy.shared.product.event.ProductCreatedEvent;
import com.mossy.shared.product.event.ProductUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendEventListener {

    private final RecommendFacade recommendFacade;
    private final RecommendMapper mapper;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void productCreatedEvent(ProductCreatedEvent event) {
        recommendFacade.syncItem(mapper.toDto(event))
            .subscribe(
                null,
                error -> {
                    if (error instanceof DomainException domainEx) {
                        log.warn("상품 임베딩 동기화 실패 [productId={}]: {} ({})",
                            event.productId(), domainEx.getMsg(), domainEx.getResultCode());
                    } else {
                        log.error("상품 임베딩 동기화 중 예상치 못한 오류 [productId={}]",
                            event.productId(), error);
                    }
                }
            );
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void productUpdatedEvent(ProductUpdatedEvent event) {
        recommendFacade.syncUpdate(event)
            .subscribe(
                null,
                error -> {
                    if (error instanceof DomainException domainEx) {
                        log.warn("상품 수정 동기화 실패 [productId={}]: {} ({})",
                            event.productId(), domainEx.getMsg(), domainEx.getResultCode());
                    } else {
                        log.error("상품 수정 동기화 중 예상치 못한 오류 [productId={}]",
                            event.productId(), error);
                    }
                }
            );
    }
}