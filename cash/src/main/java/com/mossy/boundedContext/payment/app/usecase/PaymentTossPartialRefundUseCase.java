package com.mossy.boundedContext.payment.app.usecase;

import com.mossy.boundedContext.payment.app.PaymentSupport;
import com.mossy.boundedContext.payment.domain.Payment;
import com.mossy.boundedContext.payment.in.dto.response.TossCancelResponse;
import com.mossy.boundedContext.payment.out.dto.response.MarketOrderResponse;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.kafka.KafkaTopics;
import com.mossy.kafka.outbox.service.OutboxPublisher;
import com.mossy.kafka.publisher.KafkaEventPublisher;
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
public class PaymentTossPartialRefundUseCase {

    private final PaymentSupport paymentSupport;
    private final OutboxPublisher outboxPublisher;
    private final KafkaEventPublisher kafkaEventPublisher;


    @Transactional
    public void execute(String orderId, String cancelReason, List<Long> ids, BigDecimal cancelAmount) {
        if (cancelAmount == null || cancelAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException(ErrorCode.CANCEL_AMOUNT_MUST_BE_POSITIVE);
        }

        Payment payment = paymentSupport.findPayment(orderId);
        MarketOrderResponse order = paymentSupport.findOrderForCancel(payment.getOrderId());

        // 부분 환불 API 요청 (cancelAmount 필수 포함)
        TossCancelResponse response = paymentSupport.callTossPartialCancel(payment.getPaymentKey(), cancelReason, cancelAmount);

        // 부분 환불 성공 담보 (취소 내역 저장)
        paymentSupport.processPartialCancel(payment.getOrderNo(), payment.getPaymentKey(), cancelAmount, PayMethod.CARD, cancelReason);

        PaymentCashRefundEvent cashRefundEvent = new PaymentCashRefundEvent(order.orderId(), order.buyerId(), cancelAmount, PayMethod.CARD);

        // 1. Kafka Event 직접 선행 발행
        kafkaEventPublisher.publish(cashRefundEvent);
        // 2. 캐시/잔액 복구 → Outbox 패턴으로 정합성 보장
        outboxPublisher.saveEvent(
            KafkaTopics.PAYMENT_REFUND,
            "Payment",
            payment.getId(),
            PaymentCashRefundEvent.class.getSimpleName(),
            cashRefundEvent
        );

        TossCancelPayload payload = buildPayload(response, ids);
        PaymentTossRefundEvent tossRefundEvent = new PaymentTossRefundEvent(payload);

        // 1. Kafka Event 직접 선행 발행
        kafkaEventPublisher.publish(tossRefundEvent);
        // 2. 주문 아이템 상태 업데이트 → 다른 모듈(market)이므로 Outbox 패턴
        outboxPublisher.saveEvent(
            KafkaTopics.ORDER_CANCEL,
            "Payment",
            payment.getId(),
            PaymentTossRefundEvent.class.getSimpleName(),
            tossRefundEvent
        );
    }

    private TossCancelPayload buildPayload(TossCancelResponse response, List<Long> orderItemIds) {
        List<TossCancelPayload.Cancel> cancels = response.cancels().stream()
            .map(c -> new TossCancelPayload.Cancel(c.cancelAmount(), c.cancelReason()))
            .toList();
        return new TossCancelPayload(response.orderId(), cancels, orderItemIds, "PARTIAL_CANCELED");
    }
}
