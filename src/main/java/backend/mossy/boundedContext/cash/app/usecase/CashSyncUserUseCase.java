package backend.mossy.boundedContext.cash.app.usecase;

import backend.mossy.boundedContext.cash.domain.wallet.CashUser;
import backend.mossy.boundedContext.cash.out.CashUserRepository;
import backend.mossy.boundedContext.cash.out.WalletRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.cash.dto.common.CashUserDto;
import backend.mossy.shared.cash.event.CashUserCreatedEvent;
import backend.mossy.shared.member.domain.user.UserStatus;
import backend.mossy.shared.member.dto.common.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CashSyncUserUseCase {

    private final CashUserRepository cashUserRepository;
    private final WalletRepository walletRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public CashUser syncUser(UserDto userDto) {
        // 1. CashUser 빌더를 사용하여 엔티티 생성 및 저장
        CashUser user = cashUserRepository.save(
            CashUser.builder()
                .id(userDto.id())
                .createdAt(userDto.createdAt())
                .updatedAt(userDto.updatedAt())
                .email(userDto.email())
                .name(userDto.name())
                .nickname(userDto.nickname())
                .address(userDto.address())
                .status(UserStatus.valueOf(userDto.status()))
                .build()
        );

        // 2. 해당 유저의 지갑이 없는 경우에만 지갑 생성 이벤트 발행
        if (!walletRepository.existsWalletByUser_Id(user.getId())) {
            eventPublisher.publish(
                new CashUserCreatedEvent(CashUserDto.from(user))
            );
        }

        return user;
    }
}