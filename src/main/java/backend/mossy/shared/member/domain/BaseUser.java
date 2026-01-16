package backend.mossy.shared.member.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseUser extends BaseIdAndTime {

    @Column(name = "email", nullable = false, unique = true, length = 255)
    protected String email;

    @Column(name = "name", nullable = false, length = 100)
    protected String name;

    @Column(name = "rrn_encrypted", nullable = false, unique = true, length = 255)
    protected String rrnEncrypted;

    @Column(name = "phone_num", nullable = false, length = 20)
    protected String phoneNum;

    @Column(name = "password", nullable = false, length = 255)
    protected String password;

    @Column(name = "address", nullable = false, length = 1000)
    protected String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    protected UserStatus status = UserStatus.ACTIVE;

    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    protected String nickname;

    @Column(name = "profile_image", nullable = false, length = 500)
    protected String profileImage;

    protected BaseUser(
            String email,
            String name,
            String rrnEncrypted,
            String phoneNum,
            String password,
            String address,
            String nickname,
            String profileImage
    ) {
        this.email = email;
        this.name = name;
        this.rrnEncrypted = rrnEncrypted;
        this.phoneNum = phoneNum;
        this.password = password;
        this.address = address;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.status = UserStatus.ACTIVE;
    }

    // ===== 의미 있는 변경 메서드 =====
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void changeAddress(String address) {
        this.address = address;
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void deleteMember() {
        this.status = UserStatus.DELETED;
    }
}
