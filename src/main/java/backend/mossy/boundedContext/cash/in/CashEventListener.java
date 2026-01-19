package backend.mossy.boundedContext.cash.in;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import backend.mossy.boundedContext.cash.app.CashFacade;
import backend.mossy.shared.cash.event.CashUserCreatedEvent;
import backend.mossy.shared.member.event.UserModifiedEvent;
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
    public void handleCashMemberJoinedEvent(UserJoinedEvent event) {
        cashFacade.syncUser(event.user());
    }


    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void handleCashMemberJoinedEvent(UserModifiedEvent event) {
        cashFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void handleCashMemberCreatedEvent(CashUserCreatedEvent event) {
        cashFacade.createWallet(event.user());
    }
}
