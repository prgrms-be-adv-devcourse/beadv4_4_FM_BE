package com.mossy.boundedContext.app.seller;

import com.mossy.boundedContext.domain.seller.SellerRequest;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.out.repository.seller.SellerRequestRepository;
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
