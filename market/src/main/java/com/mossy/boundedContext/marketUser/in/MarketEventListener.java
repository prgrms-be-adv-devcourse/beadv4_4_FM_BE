package com.mossy.boundedContext.marketUser.in;

import com.mossy.boundedContext.cart.app.CartFacade;
import com.mossy.boundedContext.marketUser.in.dto.event.MarketUserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class MarketEventListener {
    private final CartFacade cartFacade;

//    @TransactionalEventListener(phase = AFTER_COMMIT)
//    @Transactional(propagation = REQUIRES_NEW)
//    public void userCreatedEvent(UserJoinedEvent event) {
//        marketFacade.syncUser(event.user());
//    }
//
//    @TransactionalEventListener(phase = AFTER_COMMIT)
//    @Transactional(propagation = REQUIRES_NEW)
//    public void sellerUpdatedEvent(UserUpdatedEvent event) {
//        marketFacade.syncUser(event.user());
//    }
//
//    @TransactionalEventListener(phase = AFTER_COMMIT)
//    @Transactional(propagation = REQUIRES_NEW)
//    public void sellerCreatedEvent(SellerJoinedEvent event) {
//        marketFacade.syncSeller(event.seller());
//    }
//
//    @TransactionalEventListener(phase = AFTER_COMMIT)
//    @Transactional(propagation = REQUIRES_NEW)
//    public void sellerUpdatedEvent(SellerUpdatedEvent event) {
//        marketFacade.syncSeller(event.seller());
//    }

    @Retryable()
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void MarketCartCreatedEvent(MarketUserCreatedEvent event) {
        cartFacade.createCart(event.buyer());
    }

//    @TransactionalEventListener(phase = AFTER_COMMIT)
//    @Transactional(propagation = REQUIRES_NEW)
//    public void PaymentCompletedForOrder(PaymentCompletedEvent event) {
//        orderFacade.completePayment(event);
//    }

//    @Async
//    @TransactionalEventListener(phase = AFTER_COMMIT)
//    @Transactional(propagation = REQUIRES_NEW)
//    public void clearCartOnPaymentCompleted(OrderPaidEvent event) {
//        cartFacade.clearCart(event.buyerId());
//    }

//    @Async
//    @TransactionalEventListener(phase = AFTER_COMMIT)
//    @Transactional(propagation = REQUIRES_NEW)
//    public void useCouponsAfterPayment(CouponUseRequestedEvent event) {
//        couponFacade.useCoupons(event.userCouponIds());
//    }
}
