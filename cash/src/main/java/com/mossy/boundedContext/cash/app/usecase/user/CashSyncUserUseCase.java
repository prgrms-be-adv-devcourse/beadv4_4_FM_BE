package com.mossy.boundedContext.cash.app.usecase.user;

import com.mossy.boundedContext.cash.app.mapper.CashMapper;
import com.mossy.boundedContext.cash.domain.user.CashUser;
import com.mossy.boundedContext.cash.in.dto.common.CashUserDto;
import com.mossy.boundedContext.cash.out.user.CashUserRepository;
import com.mossy.boundedContext.cash.out.user.UserWalletRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.cash.event.CashUserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashSyncUserUseCase {

    private final CashUserRepository cashUserRepository;
    private final UserWalletRepository userWalletRepository;
    private final EventPublisher eventPublisher;
    private final CashMapper mapper;

    public CashUser syncUser(CashUserDto user) {
        CashUser cashUser = cashUserRepository.save(mapper.toEntity(user));

        if (!userWalletRepository.existsWalletByUserId(cashUser.getId())) {
            eventPublisher.publish(
                new CashUserCreatedEvent(mapper.toPayload(cashUser))
            );
        }

        return cashUser;
    }
}