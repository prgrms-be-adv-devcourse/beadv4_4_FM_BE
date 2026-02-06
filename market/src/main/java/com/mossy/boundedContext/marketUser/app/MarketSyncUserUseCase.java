package com.mossy.boundedContext.marketUser.app;

import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.boundedContext.marketUser.out.MarketUserRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.market.event.MarketUserCreatedEvent;
import com.mossy.shared.member.payload.UserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketSyncUserUseCase {
    private final MarketUserRepository marketUserRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public MarketUser syncUser(UserPayload user) {
        return marketUserRepository.findById(user.id())
            .map(existingUser -> {
                existingUser.updateUser(user);
                return existingUser;
            })
            .orElseGet(() -> {
                MarketUser newUser = marketUserRepository.save(MarketUser.from(user));
                eventPublisher.publish(new MarketUserCreatedEvent(newUser.toDto()));
                return newUser;
            });
    }
}
