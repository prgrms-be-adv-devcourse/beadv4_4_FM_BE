package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import backend.mossy.boundedContext.payout.out.payout.PayoutUserRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.member.dto.event.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [UseCase] 사용자 정보를 Payout 컨텍스트와 동기화하는 서비스 클래스
 * PayoutFacade의 '0단계: 회원/판매자 정보 동기화' 흐름에서 호출
 * Member 컨텍스트의 사용자 정보를 Payout 컨텍스트의 PayoutUser 엔티티로 복사/업데이트하여 데이터 정합성을 유지
 */
@Service
@RequiredArgsConstructor
public class PayoutSyncUserUseCase {
    private final PayoutUserRepository payoutUserRepository;

    /**
     * Member 컨텍스트로부터 받은 UserDto를 사용하여 PayoutUser 엔티티를 생성하거나 업데이트
     * 이를 통해 Payout 컨텍스트는 기부자(구매자) 정보를 자체적으로 갖게 됨
     *
     * @param user Member 컨텍스트에서 전달된 사용자 정보 DTO
     */
    @Transactional
    public void syncUser(UserDto user) {
        if (user == null || user.id() == null) {
            throw new DomainException(ErrorCode.INVALID_USER_DATA);
        }
        payoutUserRepository.save(
                PayoutUser.builder()
                        .id(user.id())
                        .email(user.email())
                        .name(user.name())
                        .address(user.address())
                        .nickname(user.nickname())
                        .latitude(user.latitude())
                        .longitude(user.longitude())
                        .profileImage(user.profileImage())
                        .createdAt(user.createdAt())
                        .updatedAt(user.updatedAt())
                        .status(user.status())
                        .build()
        );
    }
}
