package com.mossy.boundedContext.marketUser.domain;

import com.mossy.shared.market.dto.event.MarketUserDto;
import com.mossy.shared.member.domain.enums.UserStatus;
import com.mossy.shared.member.payload.UserPayload;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MARKET_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class MarketUser extends ReplicaUser {

    @Builder
    public MarketUser(
            Long id,
            String email,
            String name,
            String address,
            String nickname,
            String profileImage,
            UserStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            BigDecimal longitude,
            BigDecimal latitude
    ) {
        super(id, email, name, address, nickname, profileImage, createdAt, updatedAt, status, longitude, latitude);
    }

    public static MarketUser from(UserPayload user) {
        return MarketUser.builder()
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
    }

    public MarketUserDto toDto() {
        return MarketUserDto.builder()
                .id(getId())
                .email(getEmail())
                .name(getName())
                .address(getAddress())
                .nickname(getNickname())
                .profileImage(getProfileImage())
                .longitude(getLongitude())
                .latitude(getLatitude())
                .status(getStatus())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .build();
    }

    public void updateUser(UserPayload user) {
        super.changeUser(user);
    }
}
