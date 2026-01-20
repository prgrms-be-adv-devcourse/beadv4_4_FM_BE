package backend.mossy.shared.market.dto.event;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentOrderDto(
        Long buyerId,
        String orderNo,
        BigDecimal totalPrice
) { }
