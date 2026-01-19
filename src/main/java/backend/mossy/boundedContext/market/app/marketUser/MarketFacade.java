package backend.mossy.boundedContext.market.app.marketUser;

import backend.mossy.boundedContext.market.domain.MarketUser;
import backend.mossy.shared.member.dto.common.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketFacade {
    private final MarketSyncUserUseCase marketSyncUserUseCase;

    @Transactional
    public MarketUser syncUser(UserDto user) {
        return marketSyncUserUseCase.syncUser(user);
    }
}
