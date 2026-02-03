package com.mossy.boundedContext.app.order;

import com.mossy.boundedContext.domain.order.Order;
import com.mossy.boundedContext.domain.payment.Payment;
import com.mossy.shared.market.enums.OrderState;
import com.mossy.boundedContext.out.order.OrderRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
import com.mossy.shared.market.enums.PaymentStatus;
import com.mossy.shared.market.event.OrderCancelEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void cancelOrder(Long orderId, Long userId, String cancelReason) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getBuyer().getId().equals(userId)) {
            throw new DomainException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        if (order.getState() != OrderState.PAID) {
            throw new DomainException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        Payment paidPayment = order.getPayments().stream()
            .filter(p -> p.getStatus() == PaymentStatus.PAID)
            .findFirst()
            .orElseThrow(() -> new DomainException(ErrorCode.PAID_PAYMENT_NOT_FOUND));

        eventPublisher.publish(new OrderCancelEvent(
            order.getOrderNo(),
            order.getBuyer().getId(),
            paidPayment.getAmount(),
            paidPayment.getPayMethod(),
            cancelReason
        ));
    }
}
