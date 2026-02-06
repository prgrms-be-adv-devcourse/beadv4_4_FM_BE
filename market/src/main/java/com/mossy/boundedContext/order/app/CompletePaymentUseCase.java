package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.market.event.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompletePaymentUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    public void completePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        order.completePayment();

        eventPublisher.publish(new OrderPaidEvent(order.getBuyer().getId()));
    }
}