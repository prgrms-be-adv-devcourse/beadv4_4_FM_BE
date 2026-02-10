package com.mossy.boundedContext.marketUser.app;

import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.boundedContext.marketUser.out.MarketUserRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.boundedContext.marketUser.in.dto.event.MarketUserPayload;
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
    public void syncUser(UserPayload user) {
        marketUserRepository.findById(user.id())
            .ifPresentOrElse(
                existingUser -> existingUser.updateUser(user),
                () -> {
                    MarketUser newUser = marketUserRepository.save(MarketUser.from(user));
                    eventPublisher.publish(new MarketUserPayload(newUser.toDto()));
                }
            );
    }
}
