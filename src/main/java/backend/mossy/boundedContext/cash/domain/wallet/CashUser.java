package backend.mossy.boundedContext.cash.domain.wallet;

import backend.mossy.shared.member.domain.ReplicaMember;
<<<<<<< HEAD:src/main/java/backend/mossy/boundedContext/cash/domain/wallet/CashUser.java
import backend.mossy.shared.member.domain.ReplicaUser;
=======
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
>>>>>>> 9591ad7 ([Feature] #12 상품 등록 기능 추가 (#26)):src/main/java/backend/mossy/boundedContext/cash/domain/wallet/CashMember.java
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
