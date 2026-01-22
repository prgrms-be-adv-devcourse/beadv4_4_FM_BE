package backend.mossy.shared.market.dto.event;

import java.math.BigDecimal;

public record OrderCreateDto (
        Long OrderId,
        String orderNo,
        BigDecimal amount,
        String paymentType
)
{ }
