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

