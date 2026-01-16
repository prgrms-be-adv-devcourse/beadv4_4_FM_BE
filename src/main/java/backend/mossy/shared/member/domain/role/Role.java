package backend.mossy.shared.member.domain.role;

import backend.mossy.global.jpa.entity.BaseEntity;
import backend.mossy.global.jpa.entity.BaseIdAndTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role extends BaseIdAndTime {

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, unique = true, length = 30)
    private RoleCode code;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    public Role(RoleCode code, String name) {
        this.code = code;
        this.name = name;
    }
}
