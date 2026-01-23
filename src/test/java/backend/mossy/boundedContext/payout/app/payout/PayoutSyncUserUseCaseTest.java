package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import backend.mossy.boundedContext.payout.out.payout.PayoutUserRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.member.dto.event.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static backend.mossy.shared.member.domain.user.UserStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PayoutSyncUserUseCaseTest {

    @InjectMocks
    private PayoutSyncUserUseCase payoutSyncUserUseCase;

    @Mock
    private PayoutUserRepository payoutUserRepository;

    @Test
    @DisplayName("성공: 사용자 정보가 들어오면 빌더를 통해 PayoutUser를 생성하고 저장한다")
    void syncUser_Success() {
        // given
        Long userId = 1L;
        UserDto userDto = createUserDto(userId);

        // PayoutUser 빌더 사용
        PayoutUser savedUser = PayoutUser.builder()
                .id(userId)
                .name("홍길동")
                .build();

        given(payoutUserRepository.save(any(PayoutUser.class))).willReturn(savedUser);

        // when
        payoutSyncUserUseCase.syncUser(userDto);

        // then
        // 실제로 save()가 호출되었는지 확인
        verify(payoutUserRepository).save(any(PayoutUser.class));
    }

    @Test
    @DisplayName("실패: 유효하지 않은 사용자 데이터(null)가 들어오면 INVALID_USER_DATA 예외를 던진다")
    void syncUser_Fail_InvalidData() {
        // when & then
        assertThatThrownBy(() -> payoutSyncUserUseCase.syncUser(null))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_USER_DATA);
    }

    private UserDto createUserDto(Long userId) {
        return UserDto.builder()
                .id(userId)
                .email("test@test.com")
                .name("홍길동")
                .nickname("길동이")
                .address("서울시 강남구")
                .profileImage("profile.png")
                .status(ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}