package backend.mossy.shared.payout.dto.request;

import java.time.LocalDate;

/**
 * 정산 내역 검색 조건을 담는 DTO
 */
public record PayoutSearchRequestDto(
        LocalDate startDate,
        LocalDate endDate,
        Long memberId
        // 필요 시 아래와 같이 페이징 파라미터 추가 가능

) {
}