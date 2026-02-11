package com.mossy.boundedContext.cash.domain.user;

import com.mossy.boundedContext.cash.in.dto.command.CashUserDto;
import com.mossy.shared.member.domain.enums.UserStatus;
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
    public void update(CashUserDto cashUserDto) {
        super.update(cashUserDto);
    }
}