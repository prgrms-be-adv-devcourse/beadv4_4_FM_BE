package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.PayoutUser;
import backend.mossy.boundedContext.payout.out.PayoutUserRepository;
import backend.mossy.shared.member.dto.event.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayoutSyncUserUseCase {
    private final PayoutUserRepository payoutUserRepository;

    public PayoutUser syncUser(UserDto user) {
        return payoutUserRepository.save(
                PayoutUser.builder()
                        .id(user.id())
                        .email(user.email())
                        .name(user.name())
                        .address(user.address())
                        .nickname(user.nickname())
                        .profileImage(user.profileImage())
                        .createdAt(user.createdAt())
                        .updatedAt(user.updatedAt())
                        .status(user.status())
                        .build()
        );
    }
}
