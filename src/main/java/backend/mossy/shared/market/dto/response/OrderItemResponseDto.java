package backend.mossy.shared.market.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemResponseDto(
        Long id,
        LocalDateTime createAt,
        LocalDateTime updateAt,
        Long orderId,
        Long userId,
        String userName,
        Long sellerId,
        String sellerName,
        Long productId,
        String productName,
        BigDecimal price,
        BigDecimal payoutRate,
        BigDecimal payoutFee
) {
}