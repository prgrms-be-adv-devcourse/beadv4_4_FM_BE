package backend.mossy.shared.member.domain.user;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ReplicaUser extends BaseUser {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReplicaUser(
            Long id,
            String email,
            String name,
            String address,
            String nickname,
            String profileImage,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            UserStatus status
    ) {
        super(email, name, address, nickname, profileImage, status);
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}