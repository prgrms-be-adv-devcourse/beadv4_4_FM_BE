package com.mossy.boundedContext.payout.in.dto.event;

import com.mossy.boundedContext.payout.app.common.PayoutHandleOrderPaidUseCase;
import com.mossy.boundedContext.payout.app.PayoutFacade;
import com.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import com.mossy.boundedContext.payout.out.repository.PayoutCandidateItemRepository;
import com.mossy.shared.market.event.OrderPaidEvent;
import com.mossy.shared.market.event.OrderRefundedEvent;
import com.mossy.shared.member.event.SellerJoinedEvent;
import com.mossy.shared.member.event.SellerUpdatedEvent;
import com.mossy.shared.member.event.UserJoinedEvent;
import com.mossy.shared.member.event.UserUpdatedEvent;
import com.mossy.shared.payout.enums.PayoutEventType;
import com.mossy.shared.payout.event.PayoutCompletedEvent;
import com.mossy.shared.payout.event.PayoutSellerCreatedEvent;
import com.mossy.boundedContext.payout.out.external.dto.event.DonationLogCreateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class PayoutEventListener {
    private final PayoutFacade payoutFacade;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;
    private final ApplicationEventPublisher eventPublisher;

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
    public void payoutSellerCreatedEvent(PayoutSellerCreatedEvent event) { payoutFacade.createPayout(event.seller().sellerId());}

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void orderPaidEvent(OrderPaidEvent event) { payoutFacade.handleOrderPaid(event);}

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = REQUIRES_NEW)
    public void payoutCompletedEvent(PayoutCompletedEvent event) {
        Long payoutId = event.payout().id();

        // 1. 정산 완료된 Payout에 포함된 기부금 PayoutCandidateItem들을 조회
        List<PayoutCandidateItem> donationCandidates = payoutCandidateItemRepository
                .findByPayoutItem_Payout_IdAndEventType(payoutId, PayoutEventType.정산__상품판매_기부금);

        // 2. 각 기부금 후보 항목에 대해 기부 로그 생성 이벤트 발행
        donationCandidates.forEach(candidate -> {
            DonationLogCreateEvent donationEvent = new DonationLogCreateEvent(
                    candidate.getRelId(),           // orderItemId
                    candidate.getPayer().getId(),   // buyerId
                    candidate.getAmount(),          // 이미 계산된 기부금액
                    candidate.getCarbonKg()         // 이미 계산된 탄소 배출량 (kg)
            );
            eventPublisher.publishEvent(donationEvent);
        });

        // 3. 다음 정산을 위한 새 Payout 생성
        payoutFacade.createPayout(event.payout().payeeId());
    }
    public void orderRefundedEvent(OrderRefundedEvent event) {
        event.refundItems().forEach(refundItem -> {
           payoutFacade.processRefund(
                   refundItem.orderItemId(),
                   refundItem.refundAmount()
           );
        });
    }
}