package com.mossy.boundedContext.marketUser.app;

import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.shared.member.payload.SellerPayload;
import com.mossy.shared.member.payload.UserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketFacade {
    private final MarketSyncUserUseCase marketSyncUserUseCase;
    private final MarketSyncSellerUseCase marketSyncSellerUseCase;

    public MarketUser syncUser(UserPayload user) {
        return marketSyncUserUseCase.syncUser(user);
    }

    public MarketSeller syncSeller(SellerPayload seller) {
        return marketSyncSellerUseCase.syncSeller(seller);
    }
}
