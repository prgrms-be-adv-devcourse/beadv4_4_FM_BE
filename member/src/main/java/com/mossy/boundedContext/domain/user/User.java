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

    @Column(name = "rrn_encrypted", nullable = false, unique = true)
    protected String rrnEncrypted;

    @Column(name = "phone_num", nullable = false)
    protected String phoneNum;

    @Column(name = "password", nullable = false)
    protected String password;

   @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true ,fetch = FetchType.LAZY)
   private List<UserRole> userRoles = new ArrayList<>();

   @Builder
    public User(String email, String name, String password, String nickname,
                String address, String phoneNum, String rrnEncrypted,
                String profileImage, UserStatus status,
                BigDecimal longitude, BigDecimal latitude ) {
       super(email, name, address, nickname, profileImage, status, longitude, latitude);

       this.password = password;
       this.phoneNum = phoneNum;
       this.rrnEncrypted = rrnEncrypted;
   }

   public static User createFromOAuth2(String email, String name) {
       log.info("OAuth2 소셜 사용자 생성: email={}", email);

       // 닉네임 자동 생성 (UUID 사용)
       String nickname = generateUniqueNickname();

       return User.builder()
               .email(email)
               .name(name != null ? name : "사용자")
               .nickname(nickname)
               .password("") // 소셜 로그인 사용자는 비밀번호 없음
               .phoneNum("") // 소셜 로그인으로부터 받지 않음
               .address("") // 소셜 로그인으로부터 받지 않음
               .rrnEncrypted("") // 소셜 로그인으로부터 받지 않음
               .profileImage("default.png")
               .status(UserStatus.ACTIVE)
               .longitude(BigDecimal.ZERO)
               .latitude(BigDecimal.ZERO)
               .build();
   }

   // 중복되지 않는 고유한 닉네임을 생성합니다.
   private static String generateUniqueNickname() {
       return "user_" + UUID.randomUUID().toString().substring(0, 8);
   }

   public void addUserRole(UserRole userRole) {
      this.getUserRoles().add(userRole);
   }

   public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public RoleCode getPrimaryRole() {
       if (userRoles == null ||  userRoles.isEmpty()) return RoleCode.USER;

       return userRoles.stream()
               .map(ur -> ur.getRole().getCode())
               .max(Enum::compareTo)
               .orElse(RoleCode.USER);
    }

    public void updateProfile(String s, String address, String encryptedRrn, String nickname) {
       this.phoneNum = phoneNum;
       this.address = address;
       this.rrnEncrypted = encryptedRrn;
       this.nickname = nickname;
    }
}
