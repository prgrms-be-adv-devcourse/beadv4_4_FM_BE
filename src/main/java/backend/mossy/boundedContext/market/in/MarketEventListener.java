package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.MarketFacade;
import backend.mossy.shared.market.event.MarketUserCreatedEvent;
import backend.mossy.shared.member.event.UserJoinedEvent;
import backend.mossy.shared.member.event.UserModifiedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class MarketEventListener {
    private final MarketFacade marketFacade;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void userCreatedEvent(UserJoinedEvent event) {
        marketFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void userUpdatedEvent(UserModifiedEvent event) {
        marketFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void marketUsertCreatedEvent(MarketUserCreatedEvent event) {
        marketFacade.createCart(event.buyer());
    }
}
