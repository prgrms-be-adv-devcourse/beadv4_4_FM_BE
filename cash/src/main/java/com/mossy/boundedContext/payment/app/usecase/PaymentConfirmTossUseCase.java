package com.mossy.boundedContext.payment.app.usecase;

import com.mossy.boundedContext.cash.app.CashFacade;
import com.mossy.boundedContext.payment.app.PaymentSupport;
import com.mossy.boundedContext.payment.app.mapper.PaymentMapper;
import com.mossy.boundedContext.payment.domain.Payment;
import com.mossy.boundedContext.payment.in.dto.command.PaymentCompletedDto;
import com.mossy.boundedContext.payment.in.dto.request.PaymentConfirmTossRequestDto;
import com.mossy.boundedContext.payment.in.dto.response.TossConfirmResponse;
import com.mossy.boundedContext.payment.out.dto.response.MarketOrderResponse;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.cash.enums.PayMethod;
import com.mossy.shared.cash.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentConfirmTossUseCase {

    private final EventPublisher eventPublisher;
    private final PaymentSupport paymentSupport;
    private final CashFacade cashFacade;
    private final PaymentMapper paymentMapper;

    @Transactional
    public void confirmToss(PaymentConfirmTossRequestDto request) {
        String orderNo = PaymentSupport.resolveOriginalOrderNo(request.orderId());
        MarketOrderResponse order = paymentSupport.findPendingOrder(orderNo);

        // 1. 주문 금액 검증
        try {
            paymentSupport.validateAmount(order.totalAmount(), request.amount());
        } catch (Exception e) {
            paymentSupport.saveFailure(order.orderId(),
                orderNo, request.paymentKey(), request.amount(),
                PayMethod.CARD, "주문 금액 검증 실패: " + e.getMessage()
            );
            throw e;
        }
        // 2. PG(토스) 승인 요청
        TossConfirmResponse tossResponse;
        try {
            tossResponse = paymentSupport.requestTossConfirm(
                request.paymentKey(), request.orderId(), request.amount()
            );
        } catch (Exception e) {
            paymentSupport.saveFailure(order.orderId(),
                orderNo, request.paymentKey(), request.amount(),
                PayMethod.CARD, "PG 승인 실패: " + e.getMessage()
            );
            throw e;
        }
        // 3. 결제 완료 처리 및 이벤트 발행
        try {
            Payment payment = paymentSupport.saveTossPayment(
                order.orderId(), tossResponse.paymentKey(), orderNo, tossResponse.totalAmount()
            );

            cashFacade.cashHolding(paymentMapper.toCashHoldingRequestDto(PaymentCompletedDto.of(order,payment)));

            eventPublisher.publish(new PaymentCompletedEvent(
                order.orderId(),
                order.buyerId(),
                payment.getCreatedAt(),
                request.amount(),
                PayMethod.CASH.name())
            );

        } catch (Exception e) {
            // 시스템 오류 시 PG 결제 취소
            paymentSupport.requestTossCancel(tossResponse.paymentKey(), "시스템 오류로 인한 결제 취소");

            // DB 취소 이력 저장
            paymentSupport.processCancel(
                orderNo, tossResponse.paymentKey(), request.amount(),
                PayMethod.CARD, "시스템 오류로 인한 결제 취소: " + e.getMessage()
            );
            throw e;
        }
    }
}
