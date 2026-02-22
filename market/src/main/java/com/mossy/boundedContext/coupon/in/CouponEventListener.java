package com.mossy.boundedContext.coupon.in;

import com.mossy.boundedContext.coupon.app.CouponFacade;
import com.mossy.boundedContext.order.in.dto.event.OrderCancelEvent;
import com.mossy.boundedContext.order.in.dto.event.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponEventListener {

    private final CouponFacade couponFacade;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleted(OrderCompletedEvent event) {
        couponFacade.useCoupons(event.userCouponIds());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCancelled(OrderCancelEvent event) {
        couponFacade.restoreCoupons(event.userCouponIds());
    }
}
