package com.mossy.shared.market.event;

public record OrderCancelEvent(
    String orderNo,
    Long buyerId,
    String cancelReason
) {

}
