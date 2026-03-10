package com.mossy.boundedContext.cash.in;

import com.mossy.boundedContext.cash.app.CashFacade;
import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.kafka.KafkaTopics;
import com.mossy.shared.cash.event.PaymentCashRefundEvent;
import com.mossy.shared.member.event.SellerJoinedEvent;
import com.mossy.shared.member.event.SellerUpdatedEvent;
import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.shared.member.event.UserUpdatedEvent;
import com.mossy.shared.payout.event.PayoutSellerWalletCreditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CashKafkaEventListener {

    private final CashFacade cashFacade;
    private final CashMapper mapper;

    @KafkaListener(topics = KafkaTopics.PAYMENT_REFUND)
    public void handlePaymentRefundEvent(PaymentCashRefundEvent event) {
        try {
            cashFacade.processRefund(mapper.toCashRefundRequestDto(event));
        } catch (Exception e) {
            log.error("[Kafka] 환불 처리 실패 - orderId={}, buyerId={}, error={}",
                event.orderId(), event.buyerId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.PAYOUT_WALLET_CREDIT)
    public void handlePayoutWalletCreditEvent(PayoutSellerWalletCreditEvent event) {
        try {
            cashFacade.creditSellerBalance(mapper.toSellerBalanceRequestDto(event));
        } catch (Exception e) {
            log.error("[Kafka] 판매자 잔액 지급 실패 - error={}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.USER_JOINED)
    public void handleUserJoinedEvent(UserJoinedEvent event) {
        try {
            cashFacade.syncUser(mapper.toCashUserDto(event.user()));
        } catch (Exception e) {
            log.error("[Kafka] 사용자 동기화 실패 - error={}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.USER_UPDATED)
    public void handleUserUpdatedEvent(UserUpdatedEvent event) {
        try {
            cashFacade.syncUser(mapper.toCashUserDto(event.user()));
        } catch (Exception e) {
            log.error("[Kafka] 사용자 업데이트 동기화 실패 - error={}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.SELLER_JOINED)
    public void handleSellerJoinedEvent(SellerJoinedEvent event) {
        try {
            cashFacade.syncSeller(mapper.toCashSellerDto(event.seller()));
        } catch (Exception e) {
            log.error("[Kafka] 판매자 동기화 실패 - error={}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.SELLER_UPDATED)
    public void handleSellerUpdatedEvent(SellerUpdatedEvent event) {
        try {
            cashFacade.syncSeller(mapper.toCashSellerDto(event.seller()));
        } catch (Exception e) {
            log.error("[Kafka] 판매자 업데이트 동기화 실패 - error={}", e.getMessage(), e);
        }
    }
}
