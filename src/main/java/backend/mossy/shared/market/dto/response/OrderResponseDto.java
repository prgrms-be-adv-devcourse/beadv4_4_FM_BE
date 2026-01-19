package backend.mossy.shared.market.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponseDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long customerId,
        String customerName,
        BigDecimal price,
        LocalDateTime requestPaymentAt,
        LocalDateTime paymentAt
) {
}