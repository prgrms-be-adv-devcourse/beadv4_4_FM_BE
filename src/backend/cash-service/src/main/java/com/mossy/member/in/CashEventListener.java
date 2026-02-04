package com.mossy.member.in;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import com.mossy.member.app.CashFacade;
import com.mossy.shared.market.enums.PayMethod;
import com.mossy.shared.cash.dto.request.UserBalanceRequestDto;
import com.mossy.shared.cash.event.CashSellerCreatedEvent;
import com.mossy.shared.cash.event.CashUserCreatedEvent;
import com.mossy.shared.market.dto.toss.PaymentConfirmCashRequestDto;
import com.mossy.shared.market.event.OrderCashPaymentRequestEvent;
import com.mossy.shared.market.event.OrderCashPrePaymentEvent;
import com.mossy.shared.market.event.PaymentCompletedEvent;
import com.mossy.shared.market.event.PaymentRefundEvent;
import com.mossy.shared.member.event.SellerJoinedEvent;
import com.mossy.shared.member.event.SellerUpdatedEvent;
import com.mossy.shared.member.event.UserUpdatedEvent;
import com.mossy.shared.member.event.UserJoinedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CashEventListener {

    private final CashFacade cashFacade;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void userJoinedEvent(UserJoinedEvent event) {
        cashFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void sellerJoinedEvent(SellerJoinedEvent event) {
        cashFacade.syncSeller(event.seller());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void userUpdatedEvent(UserUpdatedEvent event) {
        cashFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void sellerUpdatedEvent(SellerUpdatedEvent event) {
        cashFacade.syncSeller(event.seller());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void cashUserCreatedEvent(CashUserCreatedEvent event) {
        cashFacade.createUserWallet(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void cashSellerCreatedEvent(CashSellerCreatedEvent event) {
        cashFacade.createSellerWallet(event.seller());
    }

    @EventListener
    public void orderCashPaymentRequestEvent(OrderCashPaymentRequestEvent event) {
        PaymentConfirmCashRequestDto request = PaymentConfirmCashRequestDto.builder()
            .orderId(event.orderNo())
            .amount(event.amount())
            .payMethod(PayMethod.CASH)
            .build();
        //paymentFacade.confirmCashPayment(request);
    }

    @EventListener
    public void orderCashPrePaymentEvent(OrderCashPrePaymentEvent event) {
        UserBalanceRequestDto request = event.toUserBalanceRequestDto();
        cashFacade.deductUserBalance(request);
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void paymentCompletedEvent(PaymentCompletedEvent event) {
        cashFacade.cashHolding(event);
    }

    @EventListener
    public void paymentRefundEvent(PaymentRefundEvent event) {
        cashFacade.processRefund(event);
    }
}