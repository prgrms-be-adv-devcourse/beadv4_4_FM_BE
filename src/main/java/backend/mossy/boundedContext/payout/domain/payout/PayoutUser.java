package backend.mossy.boundedContext.payout.domain.payout;

import backend.mossy.shared.member.domain.user.ReplicaUser;
import backend.mossy.shared.member.domain.user.UserStatus;
import backend.mossy.shared.member.dto.event.UserDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * [Domain Entity] 정산 컨텍스트 내에서 사용되는 사용자(User) 엔티티의 복제본
 * Member 컨텍스트의 {@link backend.mossy.boundedContext.member.domain.user.User} 정보를 복제하여
 * Payout 컨텍스트에서 필요한 사용자 데이터를 독립적으로 관리하고 사용
 * {@link ReplicaUser}를 상속받아 기본적인 사용자 속성을 가짐
 */
@Entity
@Table(name = "PAYOUT_USER") // Payout 컨텍스트 내에서 사용자 정보를 저장하는 테이블
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayoutUser extends ReplicaUser {

    /**
     * PayoutUser 엔티티를 생성하는 빌더 패턴 생성자
     * {@link ReplicaUser}의 생성자를 호출하여 사용자 기본 정보를 초기화
     */
    @Builder
    public PayoutUser(
            Long id,
            String email,
            String name,
            String address,
            String nickname,
            String profileImage,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            UserStatus status
    ) {
        super(id, email, name, address, nickname, profileImage, createdAt, updatedAt, status);
    }

    /**
     * 현재 PayoutUser 엔티티의 핵심 정보를 담은 DTO로 변환하여 반환
     * 주로 이벤트 발행 시 이벤트 데이터로 활용되거나 다른 서비스에 정보를 전달할 때 사용
     * @return PayoutUser의 정보를 담은 UserDto
     */
    public UserDto toDto() {
        return UserDto.builder()
                .id(getId())
                .email(getEmail())
                .name(getName())
                .address(getAddress())
                .nickname(getNickname())
                .profileImage(getProfileImage())
                .status(getStatus())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }
}
