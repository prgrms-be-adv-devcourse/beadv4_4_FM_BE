package backend.mossy.boundedContext.market.app.payment;

import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import backend.mossy.boundedContext.market.domain.payment.Payment;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.market.dto.toss.PaymentCancelCashRequestDto;
import backend.mossy.shared.market.event.OrderCancelEvent;
import backend.mossy.shared.market.event.PaymentRefundEvent;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentCancelCashUseCase {

    private final PaymentSupport paymentSupport;
    private final EventPublisher eventPublisher;

    @Transactional
    public void orderCancelCashPayment(OrderCancelEvent event) {
        String orderNo = event.orderNo();
        Payment payment = paymentSupport.findPayment(orderNo);
        BigDecimal cancelAmount = payment.getAmount();
        String cancelReason = event.cancelReason();

        Order order = paymentSupport.findOrderForCancel(orderNo);

        eventPublisher.publish(new PaymentRefundEvent(
            order.getId(),
            order.getBuyer().getId(),
            cancelAmount,
            PayMethod.CASH
        ));

        // 2. DB에 취소 기록
        paymentSupport.processCancel(orderNo, null, cancelAmount, PayMethod.CASH, cancelReason);
    }

    @Transactional
    public void cancelCashPayment(PaymentCancelCashRequestDto request) {
        String orderNo = request.orderId();
        BigDecimal cancelAmount = request.cancelAmount();
        String cancelReason = request.cancelReason();

        paymentSupport.findOrder(orderNo);
        paymentSupport.processCancel(orderNo, null, cancelAmount, PayMethod.CASH, cancelReason);
    }
}
