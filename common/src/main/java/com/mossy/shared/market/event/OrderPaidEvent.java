package com.mossy.shared.market.event;

import com.mossy.shared.market.dto.event.OrderPayoutDto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderPaidEvent(
        Long buyerId,
        List<OrderPayoutDto> orderItems,
        LocalDateTime paymentDate
) { }