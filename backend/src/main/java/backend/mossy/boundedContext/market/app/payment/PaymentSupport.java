package backend.mossy.boundedContext.market.app.payment;

import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.domain.order.OrderState;
import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import backend.mossy.boundedContext.market.domain.payment.Payment;
import backend.mossy.boundedContext.market.domain.payment.PaymentStatus;
import backend.mossy.boundedContext.market.out.order.OrderRepository;
import backend.mossy.boundedContext.market.out.payment.PaymentRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.toss.TossCancelResponse;
import backend.mossy.shared.market.dto.toss.TossConfirmRequest;
import backend.mossy.shared.market.dto.toss.TossConfirmResponse;
import backend.mossy.shared.market.event.PaymentCancelFailedEvent;
import backend.mossy.shared.market.event.PaymentCanceledEvent;
import backend.mossy.shared.market.out.TossPaymentsService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentSupport {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final TossPaymentsService tossPaymentsService;
    private final ApplicationEventPublisher eventPublisher;

    public Order findOrder(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> {return new DomainException(ErrorCode.PENDING_ORDER_NOT_FOUND);
            });
    }
    public Payment findPayment(String orderNo) {
        return paymentRepository.findByOrderNoAndStatus(orderNo, PaymentStatus.PAID)
            .orElseThrow(() -> {return new DomainException(ErrorCode.PAID_PAYMENT_NOT_FOUND);
            });
    }

    public Order findPendingOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> new DomainException(ErrorCode.PENDING_ORDER_NOT_FOUND));
        order.validatePendingState();
        return order;
    }

    public TossConfirmResponse requestTossConfirm(String paymentKey, String orderId, BigDecimal amount) {
        TossConfirmRequest request = TossConfirmRequest.of(paymentKey, orderId, amount);
        return tossPaymentsService.confirm(request);
    }

    public void requestTossCancel(String paymentKey, String cancelReason) {
        try {
            TossCancelResponse response = tossPaymentsService.cancel(paymentKey, cancelReason);
            eventPublisher.publishEvent(new PaymentCanceledEvent(response));
        } catch (Exception e) {
            eventPublisher.publishEvent(new PaymentCancelFailedEvent(paymentKey, cancelReason));
        }
    }

    public Order findOrderForCancel(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
            .filter(order -> order.getState() == OrderState.PAID)
            .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND)); // 또는 적절한 에러코드
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailure(String orderNo, String paymentKey, BigDecimal amount,
        PayMethod method, String failReason) {
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow(
            () -> { return new DomainException(ErrorCode.ORDER_NOT_FOUND);}
        );
        order.failPayment(paymentKey, amount, method, failReason);
        orderRepository.save(order);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processCancel(String orderNo, String paymentKey, BigDecimal amount,
        PayMethod method, String cancelReason) {
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow(
            () -> { return new DomainException(ErrorCode.ORDER_NOT_FOUND);}
        );
        order.cancelPayment(paymentKey, amount, method, cancelReason);
        orderRepository.save(order);
    }

    public static String resolveOriginalOrderNo(String pgOrderId) {
        if (pgOrderId == null) return null;
        int index = pgOrderId.indexOf("__");
        if (index > 0) {
            return pgOrderId.substring(0, index); // 구분자가 있을 때만 자름
        }
        return pgOrderId; // 구분자가 없으면 받은 값 그대로 반환 (프론트 수정 전 대응용)
    }
}
