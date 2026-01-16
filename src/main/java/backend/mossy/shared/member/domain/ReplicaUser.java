package backend.mossy.shared.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplicaUser extends BaseUser {
    // 조회 전용: 생성/수정 로직 두지 않음
}
