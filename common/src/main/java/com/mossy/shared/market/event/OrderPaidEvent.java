package com.mossy.shared.market.event;

public record OrderPaidEvent(
        Long buyerId
) { }