package backend.mossy.shared.market.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderCreatedResponse(
        Long orderId,
        String orderNo,
        BigDecimal totalPrice
) { }