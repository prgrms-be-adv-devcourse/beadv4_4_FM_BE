package com.mossy.boundedContext.payment.app.usecase;

import com.mossy.boundedContext.payment.app.PaymentSupport;
import com.mossy.boundedContext.payment.domain.Payment;
import com.mossy.boundedContext.payment.in.dto.response.TossCancelResponse;
import com.mossy.boundedContext.payment.out.dto.response.MarketOrderResponse;
import com.mossy.kafka.KafkaTopics;
import com.mossy.kafka.outbox.service.OutboxPublisher;
import com.mossy.shared.cash.enums.PayMethod;
import com.mossy.shared.cash.event.PaymentCashRefundEvent;
import com.mossy.shared.cash.event.PaymentTossRefundEvent;
import com.mossy.shared.cash.payload.TossCancelPayload;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentTossFullRefundUseCase {

    private final PaymentSupport paymentSupport;
    private final OutboxPublisher outboxPublisher;

    // 사용자 요청으로 인한 전체 환불 (market에 주문 상태 변경 알림 필요)
    @Transactional
    public void execute(String orderId, String cancelReason) {
        processRefund(orderId, cancelReason);
    }

    private void processRefund(String orderId, String cancelReason) {
        Payment payment = paymentSupport.findPayment(orderId);
        MarketOrderResponse order = paymentSupport.findOrderForCancel(payment.getOrderId());
        BigDecimal refundAmount = payment.getAmount();

        TossCancelResponse response = paymentSupport.callTossFullCancel(payment.getPaymentKey(), cancelReason);
        paymentSupport.updateFullCanceled(payment, cancelReason);

        // 캐시/잔액 복구 → Outbox 패턴으로 정합성 보장
        outboxPublisher.saveEvent(
            KafkaTopics.PAYMENT_REFUND,
            "Payment",
            payment.getId(),
            PaymentCashRefundEvent.class.getSimpleName(),
            new PaymentCashRefundEvent(order.orderId(), order.buyerId(), refundAmount, PayMethod.CARD)
        );

        // 주문 상태 업데이트 → 다른 모듈(market)이므로 Outbox 패턴
        TossCancelPayload payload = buildPayload(response, List.of());
        outboxPublisher.saveEvent(
            KafkaTopics.ORDER_CANCEL,
            "Payment",
            payment.getId(),
            PaymentTossRefundEvent.class.getSimpleName(),
            new PaymentTossRefundEvent(payload)
        );
    }

    private TossCancelPayload buildPayload(TossCancelResponse response, List<Long> orderItemIds) {
        List<TossCancelPayload.Cancel> cancels = response.cancels().stream()
            .map(c -> new TossCancelPayload.Cancel(c.cancelAmount(), c.cancelReason()))
            .toList();
        return new TossCancelPayload(response.orderId(), cancels, orderItemIds);
    }
}
