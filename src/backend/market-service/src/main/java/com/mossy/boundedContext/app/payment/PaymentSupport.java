package com.mossy.boundedContext.app.payment;

import com.mossy.boundedContext.domain.order.Order;
import com.mossy.boundedContext.domain.payment.Payment;
import com.mossy.boundedContext.out.order.OrderRepository;
import com.mossy.boundedContext.out.payment.PaymentRepository;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import com.mossy.shared.market.dto.toss.TossCancelResponse;
import com.mossy.shared.market.dto.toss.TossConfirmRequest;
import com.mossy.shared.market.dto.toss.TossConfirmResponse;
import com.mossy.shared.market.enums.OrderState;
import com.mossy.shared.market.enums.PayMethod;
import com.mossy.shared.market.enums.PaymentStatus;
import com.mossy.shared.market.event.PaymentCancelFailedEvent;
import com.mossy.shared.market.event.PaymentCanceledEvent;

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
