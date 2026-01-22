package backend.mossy.boundedContext.market.app.payment;

import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.global.exception.DomainException;
import backend.mossy.shared.market.dto.toss.PaymentConfirmCashRequestDto;
import backend.mossy.shared.market.event.OrderCashPrePaymentEvent;
import backend.mossy.shared.market.event.PaymentCompletedEvent;
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
                request.amount()
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
