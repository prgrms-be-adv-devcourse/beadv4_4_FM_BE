package com.mossy.boundedContext.cart.in;

import com.mossy.boundedContext.cart.app.CartFacade;
import com.mossy.boundedContext.marketUser.in.dto.event.MarketUserCreatedEvent;
import com.mossy.boundedContext.order.in.dto.event.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class CartEventListener {

    private final CartFacade cartFacade;

    @Retryable()
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void MarketCartCreatedEvent(MarketUserCreatedEvent event) {
        cartFacade.createCart(event.buyer());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleted(OrderCompletedEvent event) {
        cartFacade.clearCart(event.buyerId());
    }
}
