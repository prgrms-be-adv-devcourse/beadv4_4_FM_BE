package com.mossy.boundedContext.payout.in.dto.event;

import com.mossy.boundedContext.payout.app.PayoutFacade;
import com.mossy.shared.market.event.OrderPaidEvent;
import com.mossy.shared.market.event.OrderRefundedEvent;
import com.mossy.shared.member.event.SellerJoinedEvent;
import com.mossy.shared.member.event.SellerUpdatedEvent;
import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.shared.member.event.UserUpdatedEvent;
import com.mossy.shared.payout.event.PayoutCompletedEvent;
import com.mossy.shared.payout.event.PayoutSellerCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class PayoutEventListener {
    private final PayoutFacade payoutFacade;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void sellerJoinedEvent(SellerJoinedEvent event) {
        payoutFacade.syncSeller(event.seller());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void sellerUpdatedEvent(SellerUpdatedEvent event) {
        payoutFacade.syncSeller(event.seller());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void userJoinedEvent(UserJoinedEvent event) {
        payoutFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void userUpdatedEvent(UserUpdatedEvent event) {
        payoutFacade.syncUser(event.user());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void payoutSellerCreatedEvent(PayoutSellerCreatedEvent event) { payoutFacade.createPayout(event.seller().sellerId()); }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void orderPaidEvent(OrderPaidEvent event) { payoutFacade.handleOrderPaid(event);}

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void payoutCompletedEvent(PayoutCompletedEvent event) { payoutFacade.handlePayoutCompleted(event); }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void orderRefundedEvent(OrderRefundedEvent event) { payoutFacade.handleOrderRefunded(event); }
}