package com.mossy.boundedContext.payment.app.usecase;

import com.mossy.boundedContext.cash.app.CashFacade;
import com.mossy.boundedContext.payment.app.PaymentSupport;
import com.mossy.boundedContext.payment.app.mapper.PaymentMapper;
import com.mossy.boundedContext.payment.domain.Payment;
import com.mossy.boundedContext.payment.in.dto.command.PaymentCompletedDto;
import com.mossy.boundedContext.payment.in.dto.request.PaymentConfirmCashRequestDto;
import com.mossy.boundedContext.payment.out.dto.response.MarketOrderResponse;
import com.mossy.exception.DomainException;
import com.mossy.kafka.KafkaTopics;
import com.mossy.kafka.outbox.service.OutboxPublisher;
import com.mossy.kafka.publisher.KafkaEventPublisher;
import com.mossy.shared.cash.enums.PayMethod;
import com.mossy.shared.cash.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentConfirmCashUseCase {

    private final OutboxPublisher outboxPublisher;
    private final PaymentSupport paymentSupport;
    private final CashFacade cashFacade;
    private final PaymentMapper paymentMapper;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public void confirmCash(PaymentConfirmCashRequestDto request) {
        // 예치금 결제는 Toss PG를 거치지 않으므로 orderId에 난수 접미사(__xxx)가 붙지 않음
        String orderNo = request.orderId();
        paymentSupport.validateNoDuplicateCashPayment(orderNo);
        MarketOrderResponse order = paymentSupport.findPendingOrder(orderNo);

        try {
            paymentSupport.validateAmount(order.totalAmount(), request.amount());
        } catch (Exception e) {
            paymentSupport.saveFailure(order.orderId(),
                orderNo, null, request.amount(),
                PayMethod.CASH, "주문 금액 검증 실패: " + e.getMessage()
            );
            throw e;
        }

        try {
            Payment payment = paymentSupport.saveCashPayment(
                order.orderId(), orderNo, request.amount()
            );

            cashFacade.cashHolding(paymentMapper.toCashHoldingRequestDto(PaymentCompletedDto.of(order,payment)));

            kafkaEventPublisher.publish( new PaymentCompletedEvent(
                order.orderId(),
                order.buyerId(),
                payment.getCreatedAt(),
                request.amount(),
                PayMethod.CASH.name()
            ));
            outboxPublisher.saveEvent(
                KafkaTopics.PAYMENT_COMPLETED,
                "Payment",
                payment.getId(),
                PaymentCompletedEvent.class.getSimpleName(),
                new PaymentCompletedEvent(
                    order.orderId(),
                    order.buyerId(),
                    payment.getCreatedAt(),
                    request.amount(),
                    PayMethod.CASH.name()
                )
            );

        } catch (DomainException e) {
            paymentSupport.saveFailure(order.orderId(),
                orderNo, null, request.amount(),
                PayMethod.CASH, "예치금 결제 실패: " + e.getMessage()
            );
            throw e;
        } catch (Exception e) {
            paymentSupport.saveFailure(order.orderId(),
                orderNo, null, request.amount(),
                PayMethod.CASH, "시스템 장애: " + e.getMessage()
            );
            throw e;
        }
    }
}
