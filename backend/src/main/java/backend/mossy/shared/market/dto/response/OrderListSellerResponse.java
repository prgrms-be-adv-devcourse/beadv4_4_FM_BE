package backend.mossy.shared.market.dto.response;

import backend.mossy.boundedContext.market.domain.order.OrderState;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderListSellerResponse(
        Long orderDetailId,
        Long productId,
        int quantity,
        BigDecimal orderPrice,
        OrderState state,
        LocalDateTime createdAt
) {
}