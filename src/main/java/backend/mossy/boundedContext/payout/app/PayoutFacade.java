package backend.mossy.boundedContext.payout.app;

import backend.mossy.boundedContext.payout.domain.Payout;
import backend.mossy.boundedContext.payout.domain.PayoutCandidateItem;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.event.OrderDto;
import backend.mossy.shared.member.dto.event.SellerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class PayoutFacade {
    private final PayoutSyncSellerUseCase payoutSyncSellerUseCase;
    private final PayoutCreatePayoutUseCase payoutCreatePayoutUseCase;
    private final PayoutAddPayoutCandidateItemsUseCase payoutAddPayoutCandidateItemsUseCase;
    private final PayoutCollectPayoutItemsMoreUseCase payoutCollectPayoutItemsMoreUseCase;
    private final PayoutCompletePayoutsMoreUseCase payoutCompletePayoutsMoreUseCase;
    private final PayoutSupport payoutSupport;

    @Transactional
    public void syncSeller(SellerDto seller) {
        payoutSyncSellerUseCase.syncSeller(seller);
    }

    @Transactional
    public Payout createPayout(Long payeeId) {
        return payoutCreatePayoutUseCase.createPayout(payeeId);
    }

    @Transactional
    public void addPayoutCandidateItems(OrderDto order) {
        payoutAddPayoutCandidateItemsUseCase.addPayoutCandidateItems(order);
    }

    @Transactional
    public RsData<Integer> collectPayoutItemsMore(int limit) {
        return payoutCollectPayoutItemsMoreUseCase.collectPayoutItemsMore(limit);
    }

    @Transactional(readOnly = true)
    public List<PayoutCandidateItem> findPayoutCandidateItems() {
        return payoutSupport
                .findPayoutCandidateItems();
    }

    @Transactional
    public RsData<Integer> completePayoutsMore(int limit) {
        return payoutCompletePayoutsMoreUseCase.completePayoutsMore(limit);
    }
}
