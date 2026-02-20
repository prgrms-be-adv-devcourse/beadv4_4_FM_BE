package com.mossy.shared.market.event;

import java.util.List;

public record CouponUseRequestedEvent(List<Long> userCouponIds) {}
