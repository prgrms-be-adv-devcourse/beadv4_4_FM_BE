package backend.mossy.shared.member.domain.user;

import backend.mossy.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseUser extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true, length = 255)
    protected String email;

    @Column(name = "name", nullable = false, length = 100)
    protected String name;

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
        String address,
        String nickname,
        String profileImage,
        UserStatus status
    ) {
        this.email = email;
        this.name = name;
        this.address = address;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.status = status;
    }

    // ===== 의미 있는 변경 메서드 =====
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