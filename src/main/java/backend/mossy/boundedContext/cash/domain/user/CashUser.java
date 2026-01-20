package backend.mossy.boundedContext.cash.domain.user;

import backend.mossy.boundedContext.market.domain.MarketUser;
import backend.mossy.shared.cash.dto.event.CashUserDto;
import backend.mossy.shared.member.domain.user.ReplicaUser;
import backend.mossy.shared.member.domain.user.UserStatus;
import backend.mossy.shared.member.dto.event.UserDto;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "CASH_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class CashUser extends ReplicaUser {

    @Builder
    public CashUser(
        Long id,
        String email,
        String name,
        String address,
        String nickname,
        String profileImage,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        super(id, email, name, address, nickname, profileImage,
            createdAt, updatedAt, status);
    }

    public static CashUser from(UserDto user) {
        return CashUser.builder()
            .id(user.id())
            .email(user.email())
            .name(user.name())
            .address(user.address())
            .nickname(user.nickname())
            .profileImage(user.profileImage())
            .status(user.status())
            .createdAt(user.createdAt())
            .updatedAt(user.updatedAt())
            .build();
    }

    public CashUserDto toDto() {
        return CashUserDto.builder()
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