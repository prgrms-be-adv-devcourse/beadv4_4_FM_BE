package backend.mossy.boundedContext.payout.domain;

import backend.mossy.shared.member.domain.ReplicaMember;
import backend.mossy.shared.payout.dto.response.PayoutMemberResponseDto;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAYOUT_MEMBER")
@AttributeOverride(name = "id", column = @Column(name = "seller_id"))
@Getter
@NoArgsConstructor
public class PayoutMember extends ReplicaMember {

    public PayoutMember(Long id, LocalDateTime createDate, LocalDateTime modifyDate, String username, String password, String nickname, int activityScore) {
        super(id, createDate, modifyDate, username, password, nickname, activityScore);
    }

    /**
     * DTO 변환 책임을 PayoutMemberResponseDto의 정적 메서드에 위임합니다.
     */
    public PayoutMemberResponseDto toDto() {
        return PayoutMemberResponseDto.from(this);
    }

    public boolean isSystem() {
        return "system".equals(getUsername());
    }
}
