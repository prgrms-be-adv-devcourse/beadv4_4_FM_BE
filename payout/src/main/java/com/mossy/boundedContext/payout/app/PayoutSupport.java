package com.mossy.boundedContext.payout.app;


import com.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.boundedContext.payout.out.repository.PayoutCandidateItemRepository;
import com.mossy.boundedContext.payout.out.repository.PayoutSellerRepository;
import com.mossy.boundedContext.payout.out.repository.PayoutUserRepository;
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

    public Optional<PayoutSeller> findSystemSeller() { return payoutSellerRepository.findByStoreName("SYSTEM") ;}

    public Optional<PayoutSeller> findDonationSeller() {
        return payoutSellerRepository.findByStoreName("DONATION");
    }

    public Optional<PayoutSeller> findSellerById(Long id) {
        return payoutSellerRepository.findById(id);
    }

    public Optional<PayoutUser> findUserById(Long id) {return payoutUserRepository.findById(id);}

    public boolean existsSeller(PayoutSeller payoutSeller) {return  payoutSellerRepository.existsById(payoutSeller.getId());}

    public boolean existsUser(PayoutUser user) {return payoutUserRepository.existsById(user.getId());}

    public List<PayoutCandidateItem> findPayoutCandidateItems() {
        return payoutCandidateItemRepository.findAll();
    }
}

