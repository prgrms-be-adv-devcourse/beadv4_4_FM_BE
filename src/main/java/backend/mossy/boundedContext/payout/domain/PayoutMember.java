package backend.mossy.boundedContext.payout.domain;

import backend.mossy.shared.member.domain.ReplicaMember;
import backend.mossy.shared.payout.dto.response.PayoutMemberResponseDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAYOUT_MEMBER") // Payout 컨텍스트 전용 회원 테이블
@Getter
@NoArgsConstructor
public class PayoutMember extends ReplicaMember { // ReplicaMember를 상속받아 회원 기본 정보 복제
    public PayoutMember(Long id, LocalDateTime createDate, LocalDateTime modifyDate, String username, String password, String nickname, int activityScore) {
        super(id, createDate, modifyDate, username, password, nickname, activityScore);
    }

    /**
     * PayoutMember의 DTO(Data Transfer Object)를 생성합니다.
     * 주로 이벤트 발행 시 다른 컨텍스트로 정보를 전달하는 데 사용됩니다.
     */
    public PayoutMemberResponseDto toDto() {
        return new PayoutMemberResponseDto(
                getId(),
                getCreateDate(),
                getModifyDate(),
                getUsername(),
                getNickname(),
                getActivityScore()
        );
    }

    public boolean isSystem() {
        return getUsername().equals("system");
    }
}