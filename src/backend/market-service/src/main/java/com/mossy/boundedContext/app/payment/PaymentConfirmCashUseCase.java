package com.mossy.boundedContext.app.payment;

import com.mossy.boundedContext.domain.order.Order;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.global.exception.DomainException;
import com.mossy.shared.market.dto.toss.PaymentConfirmCashRequestDto;
import com.mossy.shared.market.enums.PayMethod;
import com.mossy.shared.market.event.OrderCashPrePaymentEvent;
import com.mossy.shared.market.event.PaymentCompletedEvent;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentConfirmCashUseCase {
    private final EventPublisher eventPublisher;
    private final PaymentSupport paymentSupport;

    @Transactional
    public void confirmCash(PaymentConfirmCashRequestDto request) {
        String orderNo = request.orderId();
        Order order = paymentSupport.findPendingOrder(orderNo);

        try {
            order.validateAmount(request.amount());
        } catch (Exception e) {
            paymentSupport.saveFailure(
                orderNo, null, request.amount(),
                PayMethod.CASH, "주문 금액 검증 실패: " + e.getMessage()
            );
            throw e;
        }

        try {
            eventPublisher.publish(new OrderCashPrePaymentEvent(
                order.getId(), order.getBuyer().getId(), request.amount()
            ));
            LocalDateTime paymentDate = order.createCashPayment(request.amount(), PayMethod.CASH);
            eventPublisher.publish(new PaymentCompletedEvent(
                order.getId(),
                order.getBuyer().getId(),
                paymentDate,
                request.amount(),
                String.valueOf(request.payMethod())
            ));
        } catch (DomainException e) {
            paymentSupport.saveFailure(
                orderNo, null, request.amount(),
                PayMethod.CASH, "예치금 결제 실패: " + e.getMessage()
            );
            throw e;
        } catch (Exception e) {
            paymentSupport.saveFailure(
                orderNo, null, request.amount(),
                PayMethod.CASH, "시스템 장애: " + e.getMessage()
            );
            throw e;
        }
    }
}
