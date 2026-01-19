package backend.mossy.boundedContext.cash.in;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import backend.mossy.boundedContext.cash.app.CashFacade;
import backend.mossy.shared.cash.event.CashSellerCreatedEvent;
import backend.mossy.shared.cash.event.CashUserCreatedEvent;
import backend.mossy.shared.member.event.SellerJoinedEvent;
import backend.mossy.shared.member.event.UserUpdatedEvent;
import backend.mossy.shared.member.event.UserJoinedEvent;
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
    public void cashUserCreatedEvent(CashUserCreatedEvent event) {
        cashFacade.createUserWallet(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void cashSellerCreatedEvent(CashSellerCreatedEvent event) {
        cashFacade.createSellerWallet(event.seller());
    }
}
