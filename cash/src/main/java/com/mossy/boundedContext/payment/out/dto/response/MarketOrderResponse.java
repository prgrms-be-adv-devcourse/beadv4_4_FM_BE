package com.mossy.boundedContext.payment.out.dto.response;

import com.mossy.shared.market.enums.OrderState;
import java.math.BigDecimal;

public record MarketOrderResponse(
    Long orderId,
    String orderNo,
    BigDecimal totalAmount,
    Long buyerId,
    OrderState status
) {}
