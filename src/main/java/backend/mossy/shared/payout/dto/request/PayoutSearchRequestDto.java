package backend.mossy.shared.payout.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 정산 내역 검색 조건을 담는 DTO
 */
@Getter
@Setter // Controller에서 Query Parameter 바인딩을 위해 Setter를 열어둘 수 있습니다.
@NoArgsConstructor
public class PayoutSearchRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long memberId;
    // 필요시 페이징 파라미터(page, size) 추가
}
