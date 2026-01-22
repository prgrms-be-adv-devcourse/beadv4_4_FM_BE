package backend.mossy.boundedContext.payout.in;

import backend.mossy.boundedContext.payout.app.PayoutFacade;
import backend.mossy.shared.member.event.SellerJoinedEvent;
import backend.mossy.shared.member.event.SellerUpdatedEvent;
import backend.mossy.shared.payout.event.PayoutCompletedEvent;
import backend.mossy.shared.payout.event.PayoutSellerCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
    public void payoutSellerCreatedEvent(PayoutSellerCreatedEvent event) {
        payoutFacade.createPayout(event.getSeller().id());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void marketOrderPaymentCompletedEvent(MarketOrderPaymentCompletedEvent event) {
        payoutFacade.addPayoutCandidateItems(event.getOrder());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void payoutCompletedEvent(PayoutCompletedEvent event) {
        payoutFacade.createPayout(event.getPayout().payeeId());
    }
}