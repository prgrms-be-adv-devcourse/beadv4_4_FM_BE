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

    @Transactional
    public void confirmCash(PaymentConfirmCashRequestDto request) {
        String orderNo = request.orderId();
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
