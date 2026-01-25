package backend.mossy.boundedContext.member.domain;

import backend.mossy.shared.member.domain.role.RoleCode;
import backend.mossy.shared.member.domain.role.UserRole;
import backend.mossy.shared.member.domain.user.SourceUser;
import backend.mossy.shared.member.domain.user.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER")
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
}
