package com.mossy.boundedContext.donation.app.common;

import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.domain.user.PayoutUser;
import com.mossy.boundedContext.payout.out.repository.PayoutSellerRepository;
import com.mossy.boundedContext.payout.out.repository.PayoutUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DonationSupport {

    private final PayoutSellerRepository payoutSellerRepository;
    private final PayoutUserRepository payoutUserRepository;


    public boolean existsSellerById(Long id) {
        return payoutSellerRepository.existsById(id);
    }

    public boolean existsUserById(Long id) {
        return payoutUserRepository.existsById(id);
    }

    public PayoutSeller getSellerReferenceById(Long id) {
        return payoutSellerRepository.getReferenceById(id);
    }

    public PayoutUser getUserReferenceById(Long id) {
        return payoutUserRepository.getReferenceById(id);
    }
}
