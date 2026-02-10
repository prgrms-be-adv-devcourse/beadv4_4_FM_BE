package com.mossy.boundedContext.cash.domain.user;

import com.mossy.shared.member.domain.enums.UserStatus;
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
            LocalDateTime updatedAt,
            BigDecimal longitude,
            BigDecimal latitude
    ) {
        super(id, email, name, address, nickname, profileImage,
            createdAt, updatedAt, status, longitude, latitude);
    }

    public void update(String name, String email, String address, String nickname,
        String profileImage, UserStatus status, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}