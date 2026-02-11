package com.mossy.boundedContext.payout.domain.user;
import com.mossy.shared.member.domain.enums.UserStatus;
import com.mossy.shared.member.payload.UserPayload;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * [Domain Entity] 정산 컨텍스트 내에서 사용되는 사용자(User) 엔티티의 복제본
 * Member 컨텍스트의 정보를 복제하여
 * Payout 컨텍스트에서 필요한 사용자 데이터를 독립적으로 관리하고 사용
 * {@link ReplicaUser}를 상속받아 기본적인 사용자 속성을 가짐
 */
@Entity
@Table(name = "PAYOUT_USER") // Payout 컨텍스트 내에서 사용자 정보를 저장하는 테이블
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayoutUser extends ReplicaUser {

    /**
     * PayoutUser 엔티티를 생성하는 빌더 패턴 생성자
     * {@link ReplicaUser}의 생성자를 호출하여 사용자 기본 정보를 초기화
     */
    @Builder
    public PayoutUser(
            Long id,
            String email,
            String name,
            String address,
            String nickname,
            String profileImage,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            UserStatus status,
            BigDecimal longitude,
            BigDecimal latitude
    ) {
        super(id, email, name, address, nickname, profileImage, createdAt, updatedAt, status, longitude, latitude);
    }

    /**
     * Member 컨텍스트로부터 받은 사용자 정보로 현재 엔티티를 동기화
     * ReplicaUser의 changeUser를 public으로 오버라이딩
     * JPA 더티 체킹을 통해 실제 변경된 필드만 DB에 반영됨
     *
     * @param user Member 컨텍스트에서 전달된 사용자 정보 DTO
     */
    @Override
    public void changeUser(UserPayload user) {
        super.changeUser(user);
    }
}
