package backend.mossy.boundedContext.cash.domain.wallet;

import backend.mossy.shared.member.domain.user.ReplicaUser;
import backend.mossy.shared.member.domain.user.UserStatus;
import jakarta.persistence.AttributeOverride;
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
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class CashUser extends ReplicaUser {

    @Builder
    public CashUser(Long id, String email, String name, String address, String nickname,
        String profileImage, LocalDateTime createdAt, LocalDateTime updatedAt,
        UserStatus status) {
        super(id, email, name, address, nickname, profileImage, createdAt, updatedAt, status);
    }
}
