package backend.mossy.boundedContext.cash.domain.wallet;

import backend.mossy.shared.member.domain.ReplicaMember;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CASH_MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class CashMember extends ReplicaMember {

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "rrn_encrypted")
    private String rrnEncrypted;

    @Column(name = "phone_num", nullable = false, length = 20)
    private String phoneNum;

    @Column(name = "password")
    private String password;

    @Column(name = "address")
    private String address;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;
}
