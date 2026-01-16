
package backend.mossy.shared.member.domain;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplicaUser extends BaseUser {
    @Id
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReplicaUser(
            Long id,
            String email,
            String name,
            String rrnEncrypted,
            String phoneNum,
            String password,
            String address,
            String nickname,
            String profileImage,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        super(email, name, rrnEncrypted, phoneNum, password, address, nickname, profileImage);
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}