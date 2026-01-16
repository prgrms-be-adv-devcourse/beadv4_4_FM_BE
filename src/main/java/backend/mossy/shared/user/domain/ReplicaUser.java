package backend.mossy.shared.user.domain;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class ReplicaUser extends BaseUser {
    @Id
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReplicaUser(String email, String name, String rrnEncrypted, String phoneNum, String password, String address, String status, String nickname, String profileImage, Long id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(email, name, rrnEncrypted, phoneNum, password, address, status, nickname, profileImage);
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
