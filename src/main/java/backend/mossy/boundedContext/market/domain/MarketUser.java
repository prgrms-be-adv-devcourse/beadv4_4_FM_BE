package backend.mossy.boundedContext.market.domain;

import backend.mossy.shared.member.domain.seller.ReplicaSeller;
import backend.mossy.shared.member.domain.user.ReplicaUser;
import backend.mossy.shared.member.domain.user.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "MARKET_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class MarketUser extends ReplicaUser {
    public MarketUser(Long id, String email, String name, String address, String nickname, String profileImage, LocalDateTime createdAt, LocalDateTime updatedAt, UserStatus status) {
        super(id, email, name, address, nickname, profileImage, createdAt, updatedAt, status);
    }
}