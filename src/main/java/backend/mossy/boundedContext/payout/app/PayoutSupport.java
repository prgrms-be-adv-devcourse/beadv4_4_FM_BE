package backend.mossy.boundedContext.payout.app;

import backend.mossy.boundedContext.payout.domain.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import backend.mossy.boundedContext.payout.out.PayoutCandidateItemRepository;
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

    public Optional<PayoutSeller> findSystemMember() {return payoutSellerRepository.findByName("system");
    }
    public Optional<PayoutSeller> findMemberById(Long id) {
        return payoutSellerRepository.findById(id);
    }

    public List<PayoutCandidateItem> findPayoutCandidateItem() {
        return payoutCandidateItemRepository.findAll();
    }

}

