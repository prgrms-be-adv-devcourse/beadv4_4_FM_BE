package backend.mossy.boundedContext.market.app.market;

import backend.mossy.boundedContext.market.domain.market.MarketUser;
import backend.mossy.boundedContext.market.out.market.MarketUserRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.market.event.MarketUserCreatedEvent;
import backend.mossy.shared.member.dto.event.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketSyncUserUseCase {
    private final MarketUserRepository marketUserRepository;
    private final EventPublisher eventPublisher;

    public MarketUser syncUser(UserDto user) {
        boolean isNew = !marketUserRepository.existsById(user.id());

        MarketUser marketUser = marketUserRepository.save(MarketUser.from(user));

        if (isNew) {
            eventPublisher.publish(new MarketUserCreatedEvent(marketUser.toDto()));
        }

        return marketUser;
    }
}
