package backend.mossy.boundedContext.payout.domain;

import backend.mossy.shared.user.domain.ReplicaUser;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAYOUT_USER")
@AttributeOverride(name = "id", column = @Column(name = "seller_id"))
@Getter
@NoArgsConstructor
public class PayoutUser extends ReplicaUser {
    private int activityScore;

    public PayoutUser(String email, String name, String rrnEncrypted, String phoneNum, String password, String address, String status, String nickname, String profileImage, Long id, LocalDateTime createdAt, LocalDateTime updatedAt, int activityScore) {
        super(email, name, rrnEncrypted, phoneNum, password, address, status, nickname, profileImage, id, createdAt, updatedAt);
        this.activityScore = activityScore;
    }

    public boolean isSystem() {
        return "system".equals(getName());
    }
}
