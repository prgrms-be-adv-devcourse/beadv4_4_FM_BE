package backend.mossy.boundedContext.cash.domain.wallet;

import backend.mossy.shared.member.domain.user.ReplicaUser;
import backend.mossy.shared.member.domain.user.UserStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CASH_MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// id 외에 에러를 유발하는 필드들을 재정의합니다.
// 추후 ReplicaUser 수정할 예정
@AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "user_id")),
    @AttributeOverride(name = "password", column = @Column(name = "password", nullable = true)),
    @AttributeOverride(name = "phoneNum", column = @Column(name = "phone_num", nullable = true)),
    @AttributeOverride(name = "rrnEncrypted", column = @Column(name = "rrn_encrypted", nullable = true)),
    @AttributeOverride(name = "profileImage", column = @Column(name = "profile_image", nullable = true)) // 추가
})
public class CashUser extends ReplicaUser {

    @Builder
    public CashUser(Long id, String email, String name, String address, String nickname,
        String profileImage, LocalDateTime createdAt, LocalDateTime updatedAt,
        UserStatus status) {
        super(id, email, name, address, nickname, profileImage, createdAt, updatedAt, status);
    }
}
