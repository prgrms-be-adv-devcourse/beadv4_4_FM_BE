package backend.mossy.shared.market.dto.event;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderDto(
        Long orderId,
        Long buyerId,
        String orderNo,
        BigDecimal totalPrice
) { }
