package backend.mossy.shared.payout.dto.request.payout;

import java.time.LocalDate;

/**
 * Payout 내역 검색 조건을 담는 데이터 전송 객체(DTO)
 */
public record PayoutSearchRequestDto(
        LocalDate startDate,
        LocalDate endDate,
        Long userId
) {
}