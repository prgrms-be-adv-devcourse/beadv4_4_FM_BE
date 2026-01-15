package backend.mossy.boundedContext.cash.domain.wallet;

import backend.mossy.shared.member.domain.ReplicaUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CASH_MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CashUser extends ReplicaUser {
    //수정 필요
}
