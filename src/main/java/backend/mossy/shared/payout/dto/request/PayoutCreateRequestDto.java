package backend.mossy.shared.payout.dto.request;

import java.math.BigDecimal;

/**
 * 정산 생성을 요청하는 DTO
 */
public record PayoutCreateRequestDto(
        // 정산받을 멤버의 ID
        Long payeeUserId,

        // 정산 요청 금액
        BigDecimal amount
) {
}