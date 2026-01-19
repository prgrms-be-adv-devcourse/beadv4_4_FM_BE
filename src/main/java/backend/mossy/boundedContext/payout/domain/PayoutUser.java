package backend.mossy.boundedContext.payout.domain;

import backend.mossy.shared.member.domain.user.ReplicaUser;
import backend.mossy.shared.member.domain.user.UserStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAYOUT_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayoutUser extends ReplicaUser {
    @Builder
    public PayoutUser(
            Long id,
            String email,
            String name,
            String address,
            String nickname,
            String profileImage,
            UserStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        super(id, email, name, address, nickname, profileImage, createdAt, updatedAt, status);
    }


    public boolean isSystem() {
        return "system".equals(getName());
    }
}
