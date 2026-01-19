package backend.mossy.shared.market.dto.event;

import java.time.LocalDateTime;

/**
 * 주문 정보를 담는 DTO (이벤트용)
 */
public record OrderDto(
        Long id,
        LocalDateTime paymentDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
