package com.mossy.boundedContext.cash.app.usecase.user;

import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.boundedContext.cash.domain.user.CashUser;
import com.mossy.boundedContext.cash.in.dto.common.CashUserDto;
import com.mossy.boundedContext.cash.out.user.CashUserRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.cash.event.CashUserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashSyncUserUseCase {

    private final CashUserRepository cashUserRepository;
    private final EventPublisher eventPublisher;
    private final CashMapper mapper;

    @Transactional
    public void syncUser(CashUserDto user) {
        cashUserRepository.findById(user.id())
            .ifPresentOrElse(
                existingUser -> existingUser.update(
                    user.name(),
                    user.email(),
                    user.address(),
                    user.nickname(),
                    user.profileImage(),
                    user.status(),
                    user.latitude(),
                    user.longitude()
                ),
                () -> {
                    CashUser newUser = cashUserRepository.save(mapper.toEntity(user));
                    eventPublisher.publish(
                        new CashUserCreatedEvent(mapper.toPayload(newUser))
                    );
                }
            );
    }
}