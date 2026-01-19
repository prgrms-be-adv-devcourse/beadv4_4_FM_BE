package backend.mossy.boundedContext.member.domain;

import backend.mossy.shared.member.domain.role.UserRole;
import backend.mossy.shared.member.domain.user.SourceUser;
import backend.mossy.shared.member.domain.user.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER_USER")
public class User extends SourceUser {

    @Column(name = "rrn_encrypted", nullable = false, unique = true)
    protected String rrnEncrypted;

    @Column(name = "phone_num", nullable = false)
    protected String phoneNum;

    @Column(name = "password", nullable = false)
    protected String password;

   @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private List<UserRole> userRoles = new ArrayList<>();

   @Builder
   public User(String email, String name, String rrnEncrypted, String phoneNum, String password, String address, String nickname, String profileImage, UserStatus status, String rrnEncrypted1, String phoneNum1, String password1) {
      super(email, name, rrnEncrypted, phoneNum, password, address, nickname, profileImage, status);
      this.rrnEncrypted = rrnEncrypted1;
      this.phoneNum = phoneNum1;
      this.password = password1;
   }

   public void addUserRole(UserRole userRole) {
      this.getUserRoles().add(userRole);
   }

   public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

}
