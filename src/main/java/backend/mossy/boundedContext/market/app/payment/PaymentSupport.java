package backend.mossy.boundedContext.market.app.payment;

import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.domain.order.OrderState;
import backend.mossy.boundedContext.market.domain.payment.PayMethod;
import backend.mossy.boundedContext.market.out.order.OrderRepository;
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
    private final TossPaymentsService tossPaymentsService;
    private final ApplicationEventPublisher eventPublisher;

    public Order findOrder(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> {return new DomainException(ErrorCode.PENDING_ORDER_NOT_FOUND);
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
        Order order = orderRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getState() != OrderState.PAID) {
            throw new DomainException(ErrorCode.INVALID_ORDER_STATE);
        }
        return order;
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
}
