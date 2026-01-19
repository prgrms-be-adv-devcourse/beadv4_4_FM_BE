package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.cart.CartFacade;
import backend.mossy.boundedContext.market.app.market.MarketFacade;
import backend.mossy.boundedContext.market.domain.MarketUser;
import backend.mossy.shared.market.event.MarketUserCreatedEvent;
import backend.mossy.shared.member.event.UserJoinedEvent;
import backend.mossy.shared.member.event.UserUpdatedEvent;
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
    private final CartFacade cartFacade;

//    @TransactionalEventListener(phase = AFTER_COMMIT)
//    @Transactional(propagation = REQUIRES_NEW)
//    public void handle(결제 이벤트) { marketFacade.decreaseProductStock(productId, 1);}

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public MarketUser userCreatedEvent(UserJoinedEvent event) {
        return marketFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public MarketUser userUpdatedEvent(UserUpdatedEvent event) {
        return marketFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void MarketCartCreatedEvent(MarketUserCreatedEvent event) {
        cartFacade.createCart(event.buyer());
    }
}
