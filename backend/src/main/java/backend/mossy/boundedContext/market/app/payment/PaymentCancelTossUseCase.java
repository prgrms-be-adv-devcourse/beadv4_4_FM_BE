package backend.mossy.boundedContext.market.app.payment;

import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import backend.mossy.boundedContext.market.domain.payment.Payment;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.shared.market.dto.toss.PaymentCancelTossRequestDto;
import backend.mossy.shared.market.event.OrderCancelEvent;
import backend.mossy.shared.market.event.PaymentRefundEvent;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentCancelTossUseCase {

    private final PaymentSupport paymentSupport;
    private final EventPublisher eventPublisher;

    @Transactional
    public void orderCancelTossPayment(OrderCancelEvent event) {
        String orderNo = event.orderNo();
        Payment payment = paymentSupport.findPayment(orderNo);
        String paymentKey = payment.getPaymentKey();
        BigDecimal cancelAmount = payment.getAmount();
        String cancelReason = event.cancelReason();

        Order order = paymentSupport.findOrderForCancel(orderNo);

        eventPublisher.publish(new PaymentRefundEvent(
            order.getId(),
            order.getBuyer().getId(),
            cancelAmount,
            PayMethod.CARD
        ));

        paymentSupport.requestTossCancel(paymentKey, cancelReason);

        paymentSupport.processCancel(orderNo, paymentKey, cancelAmount, PayMethod.CARD,
            cancelReason);
    }

    @Transactional
    public void cancelTossPayment(PaymentCancelTossRequestDto request) {
        String orderNo = request.orderId();
        String paymentKey = request.paymentKey();
        BigDecimal cancelAmount = request.cancelAmount();
        String cancelReason = request.cancelReason();

        paymentSupport.findOrderForCancel(orderNo);
        paymentSupport.requestTossCancel(paymentKey, cancelReason);
        paymentSupport.processCancel(orderNo, paymentKey, cancelAmount, PayMethod.CARD,
            cancelReason);
    }
}
