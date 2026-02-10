package com.mossy.boundedContext.cash.in;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import com.mossy.boundedContext.cash.app.CashFacade;
import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.boundedContext.payment.app.PaymentFacade;
import com.mossy.shared.cash.event.CashSellerCreatedEvent;
import com.mossy.shared.cash.event.CashUserCreatedEvent;
import com.mossy.shared.cash.event.PaymentCompletedEvent;
import com.mossy.shared.market.event.OrderCashPaymentRequestEvent;
import com.mossy.shared.market.event.OrderCashPrePaymentEvent;
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
    private final PaymentFacade paymentFacade;
    private final CashMapper mapper;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void userJoinedEvent(UserJoinedEvent event) {
        cashFacade.syncUser(mapper.toCashUserDto(event.user()));
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void sellerJoinedEvent(SellerJoinedEvent event) {
        cashFacade.syncSeller(mapper.toCashSellerDto(event.seller()));
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void userUpdatedEvent(UserUpdatedEvent event) {
        cashFacade.syncUser(mapper.toCashUserDto(event.user()));
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void sellerUpdatedEvent(SellerUpdatedEvent event) {
        cashFacade.syncSeller(mapper.toCashSellerDto(event.seller()));
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void cashUserCreatedEvent(CashUserCreatedEvent event) {
        cashFacade.createUserWallet(mapper.toCashUserDto(event.user()));
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void cashSellerCreatedEvent(CashSellerCreatedEvent event) {
        cashFacade.createSellerWallet(mapper.toCashSellerDto(event.seller()));
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void orderCashPaymentRequestEvent(OrderCashPaymentRequestEvent event) {
        paymentFacade.confirmCashPayment(mapper.toPaymentConfirmCashRequestDto(event));
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void orderCashPrePaymentEvent(OrderCashPrePaymentEvent event) {
        cashFacade.deductUserBalance(mapper.toUserBalanceRequestDto(event));
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void paymentCompletedEvent(PaymentCompletedEvent event) {
        cashFacade.cashHolding(mapper.toCashHoldingRequestDto(event));
    }

    @EventListener
    public void paymentRefundEvent(PaymentRefundEvent event) {
        cashFacade.processRefund(mapper.toCashRefundRequestDto(event));
    }
}