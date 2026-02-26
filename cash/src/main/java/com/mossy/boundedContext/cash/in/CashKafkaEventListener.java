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
        cashFacade.processRefund(mapper.toCashRefundRequestDto(event));
    }

    @KafkaListener(topics = KafkaTopics.PAYOUT_WALLET_CREDIT)
    public void handlePayoutWalletCreditEvent(PayoutSellerWalletCreditEvent event) {
        cashFacade.creditSellerBalance(mapper.toSellerBalanceRequestDto(event));
    }

    @KafkaListener(topics = KafkaTopics.USER_JOINED)
    public void handleUserJoinedEvent(UserJoinedEvent event) {
        cashFacade.syncUser(mapper.toCashUserDto(event.user()));
    }

    @KafkaListener(topics = KafkaTopics.USER_UPDATED)
    public void handleUserUpdatedEvent(UserUpdatedEvent event) {
        cashFacade.syncUser(mapper.toCashUserDto(event.user()));
    }

    @KafkaListener(topics = KafkaTopics.SELLER_JOINED)
    public void handleSellerJoinedEvent(SellerJoinedEvent event) {
        cashFacade.syncSeller(mapper.toCashSellerDto(event.seller()));
    }

    @KafkaListener(topics = KafkaTopics.SELLER_UPDATED)
    public void handleSellerUpdatedEvent(SellerUpdatedEvent event) {
        cashFacade.syncSeller(mapper.toCashSellerDto(event.seller()));
    }
}
