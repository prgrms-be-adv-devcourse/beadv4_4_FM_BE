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


   private String password;
   private String phoneNum;
   private String rrnEncrypted;

   @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   private List<UserRole> userRoles = new ArrayList<>();

   @Builder
   public User(String email, String name, String password, String nickname,
               String address, String phoneNum, String rrnEncrypted,
               String profileImage, UserStatus status) {
      super(email, name, address, nickname, profileImage, status);

      this.password = password;
      this.phoneNum = phoneNum;
      this.rrnEncrypted = rrnEncrypted;
   }

   public void addUserRole(UserRole userRole) {
      this.getUserRoles().add(userRole);
   }

}