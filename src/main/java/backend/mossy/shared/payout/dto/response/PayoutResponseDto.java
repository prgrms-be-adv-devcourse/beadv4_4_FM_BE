package backend.mossy.shared.payout.dto.response;

import backend.mossy.boundedContext.payout.domain.Payout;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PayoutResponseDto(
        Long payoutId,
        LocalDateTime createdDate,
        LocalDateTime payoutDate,
        PayoutSellerResponseDto payee,
        BigDecimal totalAmount,
        List<PayoutItemResponseDto> items
) {
    // 엔티티를 넣어주면 DTO로 바꿔서 뱉어주는 메서드
    public static PayoutResponseDto from(Payout payout) {
        return PayoutResponseDto.builder()
                .payoutId(payout.getId())
                .createdDate(payout.getCreatedAt())
                .payoutDate(payout.getPayoutDate())
                .payee(PayoutSellerResponseDto.from(payout.getPayee()))
                .totalAmount(payout.getAmount())
                .items(payout.getItems().stream()
                        .map(PayoutItemResponseDto::from)
                        .toList())
                .build();
    }
}