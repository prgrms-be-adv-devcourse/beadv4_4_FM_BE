package backend.mossy.boundedContext.market.in;

import backend.mossy.boundedContext.market.app.cart.CartFacade;
import backend.mossy.boundedContext.market.app.market.MarketFacade;
import backend.mossy.boundedContext.market.app.order.OrderFacade;
import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.boundedContext.market.domain.market.MarketUser;
import backend.mossy.shared.market.event.MarketUserCreatedEvent;
import backend.mossy.shared.market.event.PaymentCompletedEvent;
import backend.mossy.shared.member.event.SellerJoinedEvent;
import backend.mossy.shared.member.event.SellerUpdatedEvent;
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
    private final OrderFacade orderFacade;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public MarketUser userCreatedEvent(UserJoinedEvent event) {
        return marketFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public MarketUser sellerUpdatedEvent(UserUpdatedEvent event) {
        return marketFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public MarketSeller sellerCreatedEvent(SellerJoinedEvent event) {
        return marketFacade.syncSeller(event.seller());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public MarketSeller sellerUpdatedEvent(SellerUpdatedEvent event) {
        return marketFacade.syncSeller(event.seller());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void MarketCartCreatedEvent(MarketUserCreatedEvent event) {
        cartFacade.createCart(event.buyer());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void PaymentCompletedForOrder(PaymentCompletedEvent event) {
        orderFacade.completePayment(event);
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void PaymentCompletedForCart(PaymentCompletedEvent event) {
        cartFacade.clearCart(event.buyerId());
    }
}
