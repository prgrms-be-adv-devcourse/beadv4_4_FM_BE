package backend.mossy.boundedContext.payout.app;

import backend.mossy.boundedContext.payout.domain.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import backend.mossy.boundedContext.payout.out.PayoutCandidateItemRepository;
import backend.mossy.boundedContext.payout.out.PayoutRepository;
import backend.mossy.boundedContext.payout.out.PayoutSellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PayoutSupport {
    private final PayoutSellerRepository payoutSellerRepository;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;

    /**
     * 시스템 멤버(예: 플랫폼 자체)를 조회합니다.
     * 시스템 멤버는 수수료 정산의 수취인(payee)이 될 수 있습니다.
     * @return 시스템 멤버 PayoutMember (Optional)
     */
    public Optional<PayoutSeller> findSystemSeller() {
        return payoutSellerRepository.findByStoreName("system");
    }

    public Optional<PayoutSeller> findSellerById(Long id) {
        return payoutSellerRepository.findById(id);
    }

    public List<PayoutCandidateItem> findPayoutCandidateItems() {
        return payoutCandidateItemRepository.findAll();
    }
}

