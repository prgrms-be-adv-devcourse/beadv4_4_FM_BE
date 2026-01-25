package backend.mossy.shared.market.dto.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderPayoutDto(
        Long id,
        Long orderId,
        Long buyerId,
        String buyerName,
        Long sellerId,
        Long productId,
        BigDecimal orderPrice,
        String weightGrade,
        BigDecimal deliveryDistance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){ }