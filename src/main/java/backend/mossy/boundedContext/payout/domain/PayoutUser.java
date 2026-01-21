package backend.mossy.boundedContext.payout.domain;

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

@Entity
@Table(name = "PAYOUT_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayoutUser extends ReplicaUser {

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
