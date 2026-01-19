package backend.mossy.shared.market.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주문 항목 정보를 담는 DTO (이벤트용)
 */
public record OrderItemDto(
        Long id,
        String modelTypeCode,
        Long buyerId,
        Long sellerId,
        BigDecimal payoutFee,
        BigDecimal salePriceWithoutFee,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
