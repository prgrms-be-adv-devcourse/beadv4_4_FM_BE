package com.mossy.boundedContext.marketUser.app;

import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.boundedContext.marketUser.out.MarketSellerRepository;
import com.mossy.shared.member.payload.SellerPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketSyncSellerUseCase {

    private final MarketSellerRepository marketSellerRepository;

    @Transactional
    public void syncSeller(SellerPayload seller) {
        marketSellerRepository.findById(seller.sellerId())
            .ifPresentOrElse(
                existingSeller -> existingSeller.updateSeller(seller),
                () -> marketSellerRepository.save(MarketSeller.from(seller))
            );
    }
}
