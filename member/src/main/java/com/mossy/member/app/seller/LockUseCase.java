package com.mossy.member.app.seller;

import com.mossy.member.domain.seller.SellerRequest;
import com.mossy.member.exception.DomainException;
import com.mossy.member.exception.ErrorCode;
import com.mossy.member.out.seller.SellerRequestRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LockUseCase {

    private final SellerRequestRepository sellerRequestRepository;

    public SellerRequest lockAndGet(Long requestId) {
        return sellerRequestRepository.findByIdForUpdate(requestId)
                .orElseThrow(() -> new DomainException(ErrorCode.SELLER_REQUEST_NOT_FOUND));
    }
}
