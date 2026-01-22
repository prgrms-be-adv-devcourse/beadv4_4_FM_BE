package backend.mossy.shared.payout.dto.request.payout;

import java.math.BigDecimal;

/**
 * Payout 생성을 요청하기 위한 데이터 전송 객체(DTO)
 */
public record PayoutCreateRequestDto(
        // 정산받을 멤버의 ID
        Long payeeUserId,

        // 정산 요청 금액
        BigDecimal amount
) {
}