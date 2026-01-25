package backend.mossy.boundedContext.cash.app.usecase.sync;

import backend.mossy.boundedContext.cash.domain.user.CashUser;
import backend.mossy.boundedContext.cash.out.user.CashUserRepository;
import backend.mossy.boundedContext.cash.out.user.UserWalletRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.cash.event.CashUserCreatedEvent;
import backend.mossy.shared.member.dto.event.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashSyncUserUseCase {

    private final CashUserRepository cashUserRepository;
    private final UserWalletRepository userWalletRepository;
    private final EventPublisher eventPublisher;

    public CashUser syncUser(UserDto user) {
        // 1. CashUser from 메서드를 사용하여 엔티티 생성 및 저장
        CashUser cashUser = cashUserRepository.save(CashUser.from(user));

        // 2. 해당 유저의 지갑이 없는 경우에만 지갑 생성 이벤트 발행
        if (!userWalletRepository.existsWalletByUserId(cashUser.getId())) {
            eventPublisher.publish(
                new CashUserCreatedEvent(cashUser.toDto())
            );
        }

        return cashUser;
    }
}