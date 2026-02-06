package com.mossy.member.payout.app.payout;

import com.mossy.member.payout.domain.payout.PayoutUser;
import com.mossy.member.payout.domain.payout.PayoutCandidateItem;
import com.mossy.member.payout.domain.payout.PayoutSeller;
import com.mossy.member.payout.out.payout.PayoutUserRepository;
import com.mossy.member.payout.out.payout.PayoutCandidateItemRepository;
import com.mossy.member.payout.out.payout.PayoutSellerRepository;
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

