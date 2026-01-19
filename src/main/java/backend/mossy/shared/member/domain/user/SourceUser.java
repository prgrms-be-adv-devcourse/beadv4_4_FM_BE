package backend.mossy.shared.member.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class SourceUser extends BaseUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public SourceUser(
            String email,
            String name,
            String rrnEncrypted,
            String phoneNum,
            String password,
            String address,
            String nickname,
            String profileImage,
            UserStatus status
    ) {
        super(email, name, rrnEncrypted, phoneNum, password, address, nickname, profileImage, status);
    }
}