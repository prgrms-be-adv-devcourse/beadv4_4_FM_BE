package com.mossy.boundedContext.marketUser.app;

import com.mossy.shared.member.payload.SellerPayload;
import com.mossy.shared.member.payload.UserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketFacade {
    private final MarketSyncUserUseCase marketSyncUserUseCase;
    private final MarketSyncSellerUseCase marketSyncSellerUseCase;

    public void syncUser(UserPayload user) {
        marketSyncUserUseCase.syncUser(user);
    }

    public void syncSeller(SellerPayload seller) {
        marketSyncSellerUseCase.syncSeller(seller);
    }
}
