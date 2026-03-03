package com.mossy.boundedContext.order.in.dto.event;

import java.time.LocalDateTime;
import java.util.List;

public record OrderCompletedEvent(
        Long orderId,
        Long buyerId,
        LocalDateTime paidAt,
        List<Long> userCouponIds
) {}
