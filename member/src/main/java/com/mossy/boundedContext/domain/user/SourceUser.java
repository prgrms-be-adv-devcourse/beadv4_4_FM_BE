package com.mossy.boundedContext.domain.user;

import com.mossy.shared.member.domain.entity.BaseUser;
import com.mossy.shared.member.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public abstract class SourceUser extends BaseUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SourceUser(
        String email,
        String name,
        String address,
        String nickname,
        String profileImage,
        UserStatus status,
        BigDecimal longitude,
        BigDecimal latitude
    ) {
        super(email, name, address, nickname, profileImage, status, longitude, latitude);
    }
}