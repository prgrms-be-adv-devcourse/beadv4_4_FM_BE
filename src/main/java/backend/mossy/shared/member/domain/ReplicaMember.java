package backend.mossy.shared.member.domain;

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
public abstract class ReplicaMember extends BaseMember {
    @Id
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReplicaMember(Long id, String email, String name, String rrnEncrypted, String phoneNum, String password, String address, String status, String nickname, String profileImage, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(email, name, rrnEncrypted, phoneNum, password, address, status, nickname, profileImage);
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
