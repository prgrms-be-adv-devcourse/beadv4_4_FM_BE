package com.mossy.boundedContext.payout.domain;
import com.mossy.shared.member.domain.entity.BaseUser;
import com.mossy.shared.member.domain.enums.UserStatus;
import com.mossy.shared.member.dto.event.UserPayload;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ReplicaUser extends BaseUser {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ReplicaUser(
            Long id,
            String email,
            String name,
            String address,
            String nickname,
            String profileImage,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            UserStatus status,
            BigDecimal longitude,
            BigDecimal latitude
    ) {
        super(email, name, address, nickname, profileImage, status,  longitude, latitude);
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Member 컨텍스트로부터 받은 사용자 정보로 현재 엔티티의 필드를 업데이트
     * JPA 더티 체킹을 통해 실제 변경된 필드만 DB에 반영됨
     *
     * @param user Member 컨텍스트에서 전달된 사용자 정보 DTO
     */
    protected void changeUser(UserPayload user) {
        this.id = user.id();
        this.email = user.email();
        this.name = user.name();
        this.address = user.address();
        this.nickname = user.nickname();
        this.profileImage = user.profileImage();
        this.status = user.status();
        this.latitude = user.latitude();
        this.longitude = user.longitude();
        this.createdAt = user.createdAt();
        this.updatedAt = user.updatedAt();
    }
}