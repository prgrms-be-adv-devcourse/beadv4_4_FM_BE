package com.mossy.boundedContext.cash.in;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import com.mossy.boundedContext.cash.app.CashFacade;
import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.boundedContext.cash.in.dto.event.CashSellerCreatedEvent;
import com.mossy.boundedContext.cash.in.dto.event.CashUserCreatedEvent;
import com.mossy.shared.cash.event.PaymentCashRefundEvent;
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
        cashFacade.createUserWallet(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void cashSellerCreatedEvent(CashSellerCreatedEvent event) {
        cashFacade.createSellerWallet(event.seller());
    }

    @EventListener
    public void paymentRefundEvent(PaymentCashRefundEvent event) {
        cashFacade.processRefund(mapper.toCashRefundRequestDto(event));
    }
}