package com.mossy.boundedContext.coupon.in;

import com.mossy.boundedContext.coupon.app.CouponFacade;
import com.mossy.kafka.KafkaTopics;
import com.mossy.shared.market.event.CouponUseRequestedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponKafkaEventListener {

    private final CouponFacade couponFacade;

    @KafkaListener(topics = KafkaTopics.COUPON_USE_REQUESTED)
    public void handleCouponUseRequestedEvent(CouponUseRequestedEvent event) {
        couponFacade.useCoupons(event.userCouponIds());
    }
}
