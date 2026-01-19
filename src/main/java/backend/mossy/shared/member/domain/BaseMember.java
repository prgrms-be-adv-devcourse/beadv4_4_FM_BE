
package backend.mossy.shared.member.domain;

import backend.mossy.global.jpa.entity.BaseEntity;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

@MappedSuperclass
@Getter
@Setter(value = PROTECTED)
@NoArgsConstructor
public abstract class BaseMember extends BaseEntity {
    private String email;
    private String name;
    private String rrnEncrypted;
    private String phoneNum;
    private String password;
    private String address;
    private String status;
    private String nickname;
    private String profileImage;

    public BaseMember(
            String email,
            String name,
            String rrnEncrypted,
            String phoneNum,
            String password,
            String address,
            String status,
            String nickname,
            String profileImage
    ) {
        this.email = email;
        this.name = name;
        this.rrnEncrypted = rrnEncrypted;
        this.phoneNum = phoneNum;
        this.password = password;
        this.address = address;
        this.status = status;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public boolean isSystem() {
        return "system".equals(name);
    }
}