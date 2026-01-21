package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.shared.payout.dto.response.payout.PayoutCandidateItemResponse;
import backend.mossy.global.rsData.RsData;
import backend.mossy.shared.market.dto.event.OrderDto;
import backend.mossy.shared.member.dto.event.SellerDto;
import backend.mossy.shared.member.dto.event.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class PayoutFacade {
    private final PayoutSyncSellerUseCase payoutSyncSellerUseCase;
    private final PayoutSyncUserUseCase payoutSyncUserUseCase;
    private final PayoutCreatePayoutUseCase payoutCreatePayoutUseCase;
    private final PayoutAddPayoutCandidateItemsUseCase payoutAddPayoutCandidateItemsUseCase;
    private final MarketApiClient.PayoutCollectPayoutItemsMoreUseCase payoutCollectPayoutItemsMoreUseCase;
    private final PayoutCompletePayoutsMoreUseCase payoutCompletePayoutsMoreUseCase;
    private final PayoutSupport payoutSupport;

    @Transactional
    public void syncSeller(SellerDto seller) {
        payoutSyncSellerUseCase.syncSeller(seller);
    }

    @Transactional
    public void syncUser(UserDto user) {
        payoutSyncUserUseCase.syncUser(user);
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
    public List<PayoutCandidateItemResponse> findPayoutCandidateItems() {
        return payoutSupport.findPayoutCandidateItems()
                .stream()
                .map(PayoutCandidateItemResponse::from)
                .toList();
    }

    @Transactional
    public RsData<Integer> completePayoutsMore(int limit) {
        return payoutCompletePayoutsMoreUseCase.completePayoutsMore(limit);
    }
}
