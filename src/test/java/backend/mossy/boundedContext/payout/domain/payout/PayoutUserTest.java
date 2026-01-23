package backend.mossy.boundedContext.payout.domain.payout;

import backend.mossy.shared.member.domain.user.UserStatus;
import backend.mossy.shared.member.dto.event.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PayoutUserTest {

    @Test
    @DisplayName("성공: 빌더를 통해 PayoutUser가 모든 필드를 포함하여 정상적으로 생성된다")
    void create_Success() {
        // Given
        Long userId = 1L;
        String email = "test@mossy.com";
        String nickname = "모시유저";

        // When
        PayoutUser user = PayoutUser.builder()
                .id(userId)
                .email(email)
                .nickname(nickname)
                .status(UserStatus.ACTIVE)
                .build();

        // Then
        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("성공: toDto 호출 시 엔티티의 정보가 UserDto로 유실 없이 변환된다")
    void toDto_Success() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        PayoutUser user = PayoutUser.builder()
                .id(1L)
                .email("payout@mossy.com")
                .name("김철수")
                .address("서울시 강남구")
                .nickname("철수닉네임")
                .profileImage("https://image.com/profile.png")
                .status(UserStatus.ACTIVE)
                .longitude(new BigDecimal("127.0"))
                .latitude(new BigDecimal("37.0"))
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        UserDto dto = user.toDto();

        // Then
        assertThat(dto.id()).isEqualTo(user.getId());
        assertThat(dto.email()).isEqualTo(user.getEmail());
        assertThat(dto.name()).isEqualTo(user.getName());
        assertThat(dto.address()).isEqualTo(user.getAddress());
        assertThat(dto.nickname()).isEqualTo(user.getNickname());
        assertThat(dto.profileImage()).isEqualTo(user.getProfileImage());
        assertThat(dto.status()).isEqualTo(user.getStatus());
        assertThat(dto.longitude()).isEqualByComparingTo(user.getLongitude());
        assertThat(dto.latitude()).isEqualByComparingTo(user.getLatitude());
        assertThat(dto.createdAt()).isEqualTo(user.getCreatedAt());
        assertThat(dto.updatedAt()).isEqualTo(user.getUpdatedAt());
    }
}