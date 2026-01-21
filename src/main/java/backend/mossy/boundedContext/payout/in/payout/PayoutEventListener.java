package backend.mossy.boundedContext.payout.in.payout;

import backend.mossy.boundedContext.payout.app.donation.DonationFacade;
import backend.mossy.boundedContext.payout.app.payout.MarketApiClient;
import backend.mossy.boundedContext.payout.app.payout.PayoutFacade;
import backend.mossy.boundedContext.payout.in.MarketOrderPaymentCompletedEvent;
import backend.mossy.shared.member.event.SellerJoinedEvent;
import backend.mossy.shared.member.event.SellerUpdatedEvent;
import backend.mossy.shared.member.event.UserJoinedEvent;
import backend.mossy.shared.member.event.UserUpdatedEvent;
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
    private final DonationFacade donationFacade;
    private final MarketApiClient marketApiClient;

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
    public void payoutSellerCreatedEvent(PayoutSellerCreatedEvent event) {
        payoutFacade.createPayout(event.getSeller().id());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void marketOrderPaymentCompletedEvent(MarketOrderPaymentCompletedEvent event) {
        // 1. 정산 후보 항목 생성 (수수료, 판매 대금)
        payoutFacade.addPayoutCandidateItems(event.getOrder());

        // 2. 기부 로그 생성 (주문 아이템별)
        marketApiClient.getOrderItems(event.getOrder().id())
                .forEach(orderItem -> donationFacade.createDonationLog(event.getOrder(), orderItem));
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void payoutCompletedEvent(PayoutCompletedEvent event) {
        payoutFacade.createPayout(event.getPayout().payeeId());
    }
}