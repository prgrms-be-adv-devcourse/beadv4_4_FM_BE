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
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public ReplicaMember(long id, LocalDateTime createDate, LocalDateTime modifyDate, String username, String password, String nickname, int activityScore) {
        super(username, password, nickname, activityScore);
        this.id = id;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
    }
}
