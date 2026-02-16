package com.mossy.boundedContext.payout.app.common;

import com.mossy.shared.market.event.OrderRefundedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayoutHandleOrderRefundedUseCase {

    private final PayoutRefundUseCase payoutRefundUseCase;

    public void handle(OrderRefundedEvent event) {
        event.refundItems().forEach(refundItem ->
                payoutRefundUseCase.processRefund(
                        refundItem.orderItemId(),
                        refundItem.refundAmount()
                )
        );
    }
}
