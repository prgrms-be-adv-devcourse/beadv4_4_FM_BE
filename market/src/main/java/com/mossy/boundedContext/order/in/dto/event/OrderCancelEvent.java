package com.mossy.boundedContext.order.in.dto.event;

import java.util.List;

public record OrderCancelEvent(
        Long orderId,
        Long buyerId,
        List<Long> userCouponIds
) {}
