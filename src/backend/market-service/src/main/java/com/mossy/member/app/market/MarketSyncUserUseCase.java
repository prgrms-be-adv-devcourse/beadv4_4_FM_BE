package com.mossy.member.app.market;

import com.mossy.member.domain.market.MarketUser;
import com.mossy.member.out.market.MarketUserRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.market.event.MarketUserCreatedEvent;
import com.mossy.shared.member.dto.event.UserDto;
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
