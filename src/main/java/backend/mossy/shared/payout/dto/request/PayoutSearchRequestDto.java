package backend.mossy.shared.payout.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 정산 내역 검색 조건을 담는 DTO
 */
@Getter
@NoArgsConstructor
public class PayoutSearchRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long memberId;
    // 필요시 페이징 파라미터(page, size) 추가
}
