package com.mossy.boundedContext.cash.in;

import com.mossy.boundedContext.cash.app.CashFacade;
import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.kafka.KafkaTopics;
import com.mossy.shared.cash.event.PaymentCashRefundEvent;
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

    @KafkaListener(topics = "${app.kafka.topics.payment.refund}")
    public void handlePaymentRefundEvent(PaymentCashRefundEvent event) {
        cashFacade.processRefund(mapper.toCashRefundRequestDto(event));
    }

    @KafkaListener(topics = KafkaTopics.PAYOUT_WALLET_CREDIT)
    public void handlePayoutWalletCreditEvent(PayoutSellerWalletCreditEvent event) {
        cashFacade.creditSellerBalance(mapper.toSellerBalanceRequestDto(event));
    }
}
