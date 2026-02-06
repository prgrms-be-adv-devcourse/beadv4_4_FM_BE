package com.mossy.boundedContext.payout.app;


import com.mossy.boundedContext.payout.domain.PayoutCandidateItem;
import com.mossy.boundedContext.payout.domain.PayoutSeller;
import com.mossy.boundedContext.payout.domain.PayoutUser;
import com.mossy.boundedContext.payout.out.PayoutCandidateItemRepository;
import com.mossy.boundedContext.payout.out.PayoutSellerRepository;
import com.mossy.boundedContext.payout.out.PayoutUserRepository;
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

