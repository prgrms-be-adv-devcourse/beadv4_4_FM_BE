package com.mossy.boundedContext.domain.user;

import com.mossy.shared.member.domain.enums.UserStatus;
import com.mossy.shared.member.domain.role.RoleCode;
import com.mossy.boundedContext.domain.role.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USERS")
public class User extends SourceUser {

    @Column(name = "rrn_encrypted", nullable = false)
    protected String rrnEncrypted;

    @Column(name = "phone_num", nullable = false)
    protected String phoneNum;

    @Column(name = "password", nullable = false)
    protected String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserSocialAccount> socialAccounts = new ArrayList<>();

    @Builder
    public User(String email, String name, String password, String nickname,
                String address, String phoneNum, String rrnEncrypted,
                String profileImage, UserStatus status,
                BigDecimal longitude, BigDecimal latitude) {
        super(email, name, address, nickname, profileImage, status, longitude, latitude);
        this.password = password;
        this.phoneNum = phoneNum;
        this.rrnEncrypted = rrnEncrypted;
    }

    // OAuth2 소셜 로그인 신규 사용자 생성 (소셜 전용 — password/phone/rrn 없음)
    public static User createFromOAuth2(String email, String name) {
        log.info("OAuth2 소셜 사용자 생성: email={}", email);
        return User.builder()
                .email(email)
                .name(name != null ? name : "사용자")
                .nickname(generateUniqueNickname())
                .password("")
                .phoneNum("")
                .address("")
                .rrnEncrypted("")
                .profileImage("default.png")
                .status(UserStatus.ACTIVE)
                .longitude(BigDecimal.ZERO)
                .latitude(BigDecimal.ZERO)
                .build();
    }

    // 소셜 계정이 하나라도 연동된 사용자인지 확인
    public boolean isSocialUser() {
        return socialAccounts != null && !socialAccounts.isEmpty();
    }

    // 특정 provider로 연동된 소셜 계정이 있는지 확인
    public boolean hasProvider(String provider) {
        return socialAccounts != null && socialAccounts.stream()
                .anyMatch(sa -> sa.getProvider().equals(provider));
    }

    private static String generateUniqueNickname() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public void addUserRole(UserRole userRole) {
        this.userRoles.add(userRole);
    }

    public void addSocialAccount(UserSocialAccount socialAccount) {
        this.socialAccounts.add(socialAccount);
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public RoleCode getPrimaryRole() {
        if (userRoles == null || userRoles.isEmpty()) return RoleCode.USER;
        return userRoles.stream()
                .map(ur -> ur.getRole().getCode())
                .max(Enum::compareTo)
                .orElse(RoleCode.USER);
    }

    public void updateProfile(String phoneNum, String address, String encryptedRrn, String nickname) {
        this.phoneNum = phoneNum;
        this.address = address;
        this.rrnEncrypted = encryptedRrn;
        this.nickname = nickname;
    }
}
