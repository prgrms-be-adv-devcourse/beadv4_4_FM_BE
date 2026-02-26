package com.mossy.boundedContext.payment.app.usecase;

import com.mossy.boundedContext.payment.app.PaymentSupport;
import com.mossy.boundedContext.payment.domain.Payment;
import com.mossy.boundedContext.payment.out.dto.response.MarketOrderResponse;
import com.mossy.kafka.KafkaTopics;
import com.mossy.kafka.outbox.service.OutboxPublisher;
import com.mossy.kafka.publisher.KafkaEventPublisher;
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
public class PaymentCashFullRefundUseCase {

    private final PaymentSupport paymentSupport;
    private final OutboxPublisher outboxPublisher;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public void execute(String orderId, String cancelReason) { // 파라미터 변경
        Payment payment = paymentSupport.findPayment(orderId);
        MarketOrderResponse order = paymentSupport.findOrderForCancel(payment.getOrderId());

        PaymentCashRefundEvent cashRefundEvent = new PaymentCashRefundEvent(order.orderId(), order.buyerId(), payment.getAmount(), PayMethod.CASH);

        // 1. Kafka Event 직접 발행
        kafkaEventPublisher.publish(cashRefundEvent);
        // 2. 캐시/잔액 복구 → Outbox 패턴으로 정합성 보장
        outboxPublisher.saveEvent(
            KafkaTopics.PAYMENT_REFUND,
            "Payment",
            payment.getId(),
            PaymentCashRefundEvent.class.getSimpleName(),
            cashRefundEvent
        );

        TossCancelPayload payload = new TossCancelPayload(
            payment.getOrderNo(),
            List.of(new TossCancelPayload.Cancel(payment.getAmount(), cancelReason)),
            List.of(),
            "CANCELED"
        );
        PaymentTossRefundEvent tossRefundEvent = new PaymentTossRefundEvent(payload);

        // 1. Kafka Event 직접 발행
        kafkaEventPublisher.publish(tossRefundEvent);
        // 2. 주문 상태 업데이트 → Outbox 패턴
        outboxPublisher.saveEvent(
            KafkaTopics.ORDER_CANCEL,
            "Payment",
            payment.getId(),
            PaymentTossRefundEvent.class.getSimpleName(),
            tossRefundEvent
        );

        paymentSupport.updateFullCanceled(payment, cancelReason);
    }
}
