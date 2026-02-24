package com.mossy.boundedContext.payout.app.common;

import com.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import com.mossy.boundedContext.payout.out.external.dto.event.DonationLogCreateEvent;
import com.mossy.boundedContext.payout.out.repository.PayoutCandidateItemRepository;
import com.mossy.shared.cash.enums.SellerEventType;
import com.mossy.shared.payout.event.PayoutCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayoutHandlePayoutCompletedUseCase {

    private final PayoutCandidateItemRepository payoutCandidateItemRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PayoutCreatePayoutUseCase payoutCreatePayoutUseCase;

    public void handle(PayoutCompletedEvent event) {
        Long payoutId = event.payout().id();

        List<PayoutCandidateItem> donationCandidates = payoutCandidateItemRepository
                .findByPayoutItem_Payout_IdAndEventType(
                        payoutId,
                        SellerEventType.정산__상품판매_기부금
                );

        donationCandidates.forEach(candidate -> {
            DonationLogCreateEvent donationEvent = new DonationLogCreateEvent(
                    candidate.getRelId(),
                    candidate.getPayer().getId(),
                    candidate.getAmount(),
                    candidate.getCarbonKg()
            );
            eventPublisher.publishEvent(donationEvent);
        });

        payoutCreatePayoutUseCase.createPayout(event.payout().payeeId());
    }
}
