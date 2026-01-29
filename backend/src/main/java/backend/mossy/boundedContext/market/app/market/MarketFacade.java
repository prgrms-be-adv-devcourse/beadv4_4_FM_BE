package backend.mossy.boundedContext.market.app.market;

import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.boundedContext.market.domain.market.MarketUser;
import backend.mossy.shared.member.dto.event.SellerApprovedEvent;
import backend.mossy.shared.member.dto.event.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketFacade {
    private final MarketSyncUserUseCase marketSyncUserUseCase;
    private final MarketSyncSellerUseCase marketSyncSellerUseCase;

    @Transactional
    public MarketUser syncUser(UserDto user) {
        return marketSyncUserUseCase.syncUser(user);
    }

    @Transactional
    public MarketSeller syncSeller(SellerApprovedEvent seller) {
        return marketSyncSellerUseCase.syncSeller(seller);
    }
}
