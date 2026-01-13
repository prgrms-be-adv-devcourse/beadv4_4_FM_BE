package backend.mossy.shared.member.domain;

import backend.mossy.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

@MappedSuperclass
@Getter
@Setter(value = PROTECTED)
@NoArgsConstructor
public abstract class BaseMember extends BaseEntity {
    @Column(unique = true)
    private String username;
    private String password;
    private String nickname;
    private int activityScore;

    public BaseMember(String username, String password, String nickname, int activityScore) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.activityScore = activityScore;
    }

    public boolean isSystem() {
        return "system".equals(username);
    }
}
