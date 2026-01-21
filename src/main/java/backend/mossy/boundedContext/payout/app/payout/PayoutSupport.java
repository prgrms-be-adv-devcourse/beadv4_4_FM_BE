package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.PayoutUser;
import backend.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import backend.mossy.boundedContext.payout.domain.PayoutSeller;
import backend.mossy.boundedContext.payout.out.PayoutUserRepository;
import backend.mossy.boundedContext.payout.out.payout.PayoutCandidateItemRepository;
import backend.mossy.boundedContext.payout.out.PayoutSellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PayoutSupport {
    private final PayoutSellerRepository payoutSellerRepository;
    private final PayoutUserRepository payoutUserRepository;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;

    public Optional<PayoutSeller> findSystemSeller() {
        return payoutSellerRepository.findByStoreName("system");
    }

    public Optional<PayoutSeller> findDonationSeller() {
        return payoutSellerRepository.findByStoreName("DONATION");
    }

    public Optional<PayoutSeller> findSellerById(Long id) {
        return payoutSellerRepository.findById(id);
    }

    public Optional<PayoutUser> findUserById(Long id) {
        return payoutUserRepository.findById(id);
    }

    public List<PayoutCandidateItem> findPayoutCandidateItems() {
        return payoutCandidateItemRepository.findAll();
    }
}

