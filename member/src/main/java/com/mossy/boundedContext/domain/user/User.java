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
        this.phoneNum = phoneNum != null ? phoneNum : "";
        this.rrnEncrypted = rrnEncrypted != null ? rrnEncrypted : "";
    }

    //소셜 로그인 신규 사용자 생성 - 추가정보 입력 완료 전까지 PENDING 상태
    public static User createFromOAuth2(String email, String name) {
        log.info("OAuth2 소셜 사용자 생성(PENDING): email={}", email);
        return User.builder()
                .email(email)
                .name(name != null ? name : "사용자")
                .nickname("user_" + UUID.randomUUID().toString().substring(0, 8))
                .password("")
                .phoneNum("")
                .address("")
                .rrnEncrypted("")
                .profileImage("default-user")
                .status(UserStatus.PENDING)
                .longitude(BigDecimal.ZERO)
                .latitude(BigDecimal.ZERO)
                .build();
    }

    public void addUserRole(UserRole userRole) {
        this.userRoles.add(userRole);
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

    public void changeAddress(String encryptedAddress) {
        this.address = encryptedAddress;
    }

    public void changePhoneNum(String encryptedPhoneNum) {
        this.phoneNum = encryptedPhoneNum;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    // 소셜 로그인 전용 계정 여부 (비밀번호가 비어있으면 소셜 전용)
    public boolean isSocialOnly() {
        return this.password == null || this.password.isBlank();
    }
}
