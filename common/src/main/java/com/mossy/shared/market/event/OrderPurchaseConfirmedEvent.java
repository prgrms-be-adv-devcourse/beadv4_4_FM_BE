package com.mossy.shared.market.event;

import java.util.List;

public record OrderPurchaseConfirmedEvent(
    List<Long> orderItemIds
) {
}
