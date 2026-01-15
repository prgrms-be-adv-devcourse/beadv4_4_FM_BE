package backend.mossy.boundedContext.market.domain;

import backend.mossy.global.jpa.entity.BaseIdAndTime;
import backend.mossy.shared.member.domain.BaseMember;
import backend.mossy.shared.member.domain.ReplicaMember;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MARKET_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class MarketUser extends ReplicaMember {

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