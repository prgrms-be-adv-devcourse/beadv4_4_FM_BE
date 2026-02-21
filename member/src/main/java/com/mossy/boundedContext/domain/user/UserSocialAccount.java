package com.mossy.boundedContext.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 한 User 계정에 여러 소셜 로그인(google, kakao 등)을 연동하기 위한 엔티티
 * - user_id + provider 조합으로 unique 보장
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "USER_SOCIAL_ACCOUNTS",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_user_provider",
        columnNames = {"user_id", "provider"}
    )
)
public class UserSocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 소셜 플랫폼 종류 (google, kakao)
    @Column(name = "provider", nullable = false, length = 20)
    private String provider;

    // 소셜 플랫폼에서 발급한 고유 ID
    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;

    // 소셜 플랫폼에서 받아온 이메일 (null 가능)
    @Column(name = "social_email", length = 255)
    private String socialEmail;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public UserSocialAccount(User user, String provider, String providerId, String socialEmail) {
        this.user = user;
        this.provider = provider;
        this.providerId = providerId;
        this.socialEmail = socialEmail;
    }
}

