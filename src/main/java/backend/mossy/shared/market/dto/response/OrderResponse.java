package backend.mossy.shared.market.dto.response;

import backend.mossy.boundedContext.market.domain.order.Order;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderResponse(
        Long orderId,
        String orderNo,
        BigDecimal totalPrice,
        String buyerName,
        String address
) {
    public static OrderResponse of(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .totalPrice(order.getTotalPrice())
                .buyerName(order.getBuyer().getName())
                .address(order.getAddress())
                .build();
    }
}