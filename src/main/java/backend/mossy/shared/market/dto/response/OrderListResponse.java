package backend.mossy.shared.market.dto.response;

import backend.mossy.boundedContext.market.domain.order.OrderState;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderListResponse(
        Long orderId,
        String orderNo,
        BigDecimal totalPrice,
        OrderState state,
        Long itemCount,
        String address,
        LocalDateTime createdAt
) {
}