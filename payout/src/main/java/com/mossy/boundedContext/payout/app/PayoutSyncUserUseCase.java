package com.mossy.boundedContext.payout.app;

import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
import com.mossy.boundedContext.payout.domain.PayoutUser;
import com.mossy.boundedContext.payout.out.PayoutUserRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.member.dto.event.UserPayload;
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
    private final EventPublisher eventPublisher;

    /**
     * Member 컨텍스트로부터 받은 UserDto를 사용하여 PayoutUser 엔티티를 생성하거나 업데이트
     * 기존 엔티티가 있으면 changeUser로 업데이트하고, 없으면 새로 생성
     * JPA 더티 체킹을 통해 실제 변경된 필드만 DB에 반영됨
     *
     * @param user Member 컨텍스트에서 전달된 사용자 정보 DTO
     */
    @Transactional
    public void syncUser(UserPayload user) {
        if (user == null || user.id() == null) {
            throw new DomainException(ErrorCode.INVALID_USER_DATA);
        }

        payoutUserRepository.findById(user.id())
                .ifPresentOrElse(
                        // 기존 사용자: changeUser로 업데이트 (더티 체킹으로 변경된 필드만 UPDATE)
                        existingUser -> existingUser.changeUser(user),
                        // 새 사용자: 엔티티 생성
                        () -> {
                            PayoutUser newUser = PayoutUser.builder()
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
                                    .build();

                            payoutUserRepository.save(newUser);
                        }
                );
    }
}
