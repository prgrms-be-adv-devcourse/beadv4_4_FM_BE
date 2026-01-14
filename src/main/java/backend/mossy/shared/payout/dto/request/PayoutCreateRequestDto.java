package backend.mossy.shared.payout.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 정산 생성을 요청하는 DTO
 */
@Getter
@NoArgsConstructor
public class PayoutCreateRequestDto {
    // 정산받을 멤버의 ID
    private Long payeeMemberId;

    // 정산 요청 금액
    private BigDecimal amount;
}
