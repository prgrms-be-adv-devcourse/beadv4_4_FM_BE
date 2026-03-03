package com.mossy.boundedContext.cash.in;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import com.mossy.boundedContext.cash.app.CashFacade;
import com.mossy.boundedContext.cash.in.dto.event.CashSellerCreatedEvent;
import com.mossy.boundedContext.cash.in.dto.event.CashUserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CashEventListener {

    private final CashFacade cashFacade;

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

}