package backend.mossy.boundedContext.market.app;

import backend.mossy.boundedContext.market.domain.MarketUser;
import backend.mossy.boundedContext.market.out.MarketUserRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.market.event.MarketUserCreatedEvent;
import backend.mossy.shared.member.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketSyncUserUseCase {
    private final MarketUserRepository marketUserRepository;
    private final EventPublisher eventPublisher;

    public MarketUser syncUser(UserDto user) {
        boolean isNew = !marketUserRepository.existsById(user.id());

        MarketUser marketUser = marketUserRepository.save(
            MarketUser.builder()
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

        if (isNew) {
            eventPublisher.publish(
                    new MarketUserCreatedEvent(
                            marketUser.toDto()
                    )
            );
        }

        return marketUser;
    }
}
