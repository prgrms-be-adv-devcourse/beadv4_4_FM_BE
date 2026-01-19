package backend.mossy.boundedContext.cash.app.usecase;

import backend.mossy.boundedContext.cash.domain.wallet.CashUser;
import backend.mossy.boundedContext.cash.out.CashUserRepository;
import backend.mossy.boundedContext.cash.out.WalletRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.cash.event.CashUserCreatedEvent;
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
    public CashUser syncUser(UserDto user) {
        // 1. CashUser 빌더를 사용하여 엔티티 생성 및 저장
        CashUser cashUser = cashUserRepository.save(
            CashUser.builder()
                .id(user.id())
                .email(user.email())
                .name(user.name())
                .rrnEncrypted(user.rrnEncrypted())
                .phoneNum(user.phoneNum())
                .password(user.password())
                .address(user.address())
                .nickname(user.nickname())
                .profileImage(user.profileImage())
                .status(user.status())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .build()
        );

        // 2. 해당 유저의 지갑이 없는 경우에만 지갑 생성 이벤트 발행
        if (!walletRepository.existsWalletByUserId(cashUser.getId())) {
            eventPublisher.publish(
                new CashUserCreatedEvent(cashUser.toDto())
            );
        }

        return cashUser;
    }
}