package com.mossy.boundedContext.coupon.domain.event;

import java.util.List;

public record CouponUseRequestedEvent(List<Long> userCouponIds) {}
