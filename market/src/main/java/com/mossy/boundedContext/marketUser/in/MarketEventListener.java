package com.mossy.boundedContext.marketUser.in;

import com.mossy.boundedContext.cart.app.CartFacade;
import com.mossy.boundedContext.coupon.app.CouponFacade;
import com.mossy.boundedContext.coupon.domain.event.CouponUseRequestedEvent;
import com.mossy.boundedContext.marketUser.app.MarketFacade;
import com.mossy.boundedContext.marketUser.in.dto.event.MarketUserCreatedEvent;
import com.mossy.boundedContext.order.app.OrderFacade;
import com.mossy.shared.cash.event.PaymentCompletedEvent;
import com.mossy.shared.market.event.OrderPaidEvent;
import com.mossy.shared.member.event.SellerJoinedEvent;
import com.mossy.shared.member.event.SellerUpdatedEvent;
import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.shared.member.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;
import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

@Component
@RequiredArgsConstructor
public class MarketEventListener {
    private final MarketFacade marketFacade;
    private final CartFacade cartFacade;
    private final OrderFacade orderFacade;
    private final CouponFacade couponFacade;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void userCreatedEvent(UserJoinedEvent event) {
        marketFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void sellerUpdatedEvent(UserUpdatedEvent event) {
        marketFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void sellerCreatedEvent(SellerJoinedEvent event) {
        marketFacade.syncSeller(event.seller());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void sellerUpdatedEvent(SellerUpdatedEvent event) {
        marketFacade.syncSeller(event.seller());
    }

    @EventListener
    public void MarketCartCreatedEvent(MarketUserCreatedEvent event) {
        cartFacade.createCart(event.buyer());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void PaymentCompletedForOrder(PaymentCompletedEvent event) {
        orderFacade.completePayment(event);
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void clearCartOnPaymentCompleted(OrderPaidEvent event) {
        cartFacade.clearCart(event.buyerId());
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void useCouponsAfterPayment(CouponUseRequestedEvent event) {
        couponFacade.useCoupons(event.userCouponIds());
    }
}
