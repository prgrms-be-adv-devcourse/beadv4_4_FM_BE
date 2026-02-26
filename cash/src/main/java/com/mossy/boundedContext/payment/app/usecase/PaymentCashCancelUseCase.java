package com.mossy.boundedContext.payment.app.usecase;

import com.mossy.boundedContext.payment.app.PaymentSupport;
import com.mossy.boundedContext.payment.domain.Payment;
import com.mossy.boundedContext.payment.in.dto.request.PaymentCancelCashRequestDto;
import com.mossy.boundedContext.payment.out.dto.response.MarketOrderResponse;
import com.mossy.kafka.KafkaTopics;
import com.mossy.kafka.outbox.service.OutboxPublisher;
import com.mossy.shared.cash.enums.PayMethod;
import com.mossy.shared.cash.event.PaymentCashRefundEvent;
import com.mossy.shared.cash.event.PaymentTossRefundEvent;
import com.mossy.shared.cash.payload.TossCancelPayload;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCashCancelUseCase {

    private final PaymentSupport paymentSupport;
    private final OutboxPublisher outboxPublisher;

    // 사용자 요청으로 인한 캐시 결제 전체 취소
    @Transactional
    public void cancelCashPayment(PaymentCancelCashRequestDto request) {
        String orderNo = request.orderId();
        String cancelReason = request.cancelReason();

        Payment payment = paymentSupport.findPayment(orderNo);
        MarketOrderResponse order = paymentSupport.findOrderForCancel(payment.getOrderId());

        // 캐시/잔액 복구 → Outbox 패턴으로 정합성 보장 (실제 결제 금액 기준으로 환불)
        outboxPublisher.saveEvent(
            KafkaTopics.PAYMENT_REFUND,
            "Payment",
            payment.getId(),
            PaymentCashRefundEvent.class.getSimpleName(),
            new PaymentCashRefundEvent(order.orderId(), order.buyerId(), payment.getAmount(), PayMethod.CASH)
        );

        // 주문 상태 업데이트 → 다른 모듈(market)이므로 Outbox 패턴
        TossCancelPayload payload = new TossCancelPayload(
            payment.getOrderNo(),
            List.of(new TossCancelPayload.Cancel(payment.getAmount(), cancelReason)),
            List.of()
        );
        outboxPublisher.saveEvent(
            KafkaTopics.ORDER_CANCEL,
            "Payment",
            payment.getId(),
            PaymentTossRefundEvent.class.getSimpleName(),
            new PaymentTossRefundEvent(payload)
        );

        paymentSupport.updateFullCanceled(payment, cancelReason);
    }
}
