package backend.mossy.boundedContext.payout.in.payout;

import backend.mossy.boundedContext.payout.app.donation.DonationFacade;
import backend.mossy.boundedContext.payout.app.payout.PayoutFacade;
import backend.mossy.shared.cash.event.PaymentCompletedEvent;
import backend.mossy.shared.market.out.MarketApiClient;
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
        payoutFacade.createPayout(event.seller().id());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void paymentCompletedEvent(PaymentCompletedEvent event) {
        // Payment 도메인에서 결제 완료 시 발행되는 이벤트 처리
        // API로 OrderItem들을 조회하여 정산 후보 생성 및 기부 로그 생성
        marketApiClient.getOrderItems(event.orderId())
                .forEach(orderItem -> {
                    // 1. 정산 후보 항목 생성 (수수료, 판매 대금, 기부금)
                    payoutFacade.addPayoutCandidateItem(orderItem, event.paymentDate());

                    // 2. 기부 로그 생성
                    donationFacade.createDonationLog(orderItem);
                });
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void payoutCompletedEvent(PayoutCompletedEvent event) {
        // 1. 정산 완료된 기부 로그 업데이트
        donationFacade.settleDonationLogs(event.payout().id());

        // 2. 다음 정산을 위한 새 Payout 생성
        payoutFacade.createPayout(event.payout().payeeId());
    }
}