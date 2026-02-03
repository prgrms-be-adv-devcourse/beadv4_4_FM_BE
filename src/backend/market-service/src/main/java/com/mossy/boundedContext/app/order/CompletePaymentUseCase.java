package com.mossy.boundedContext.app.order;

import com.mossy.boundedContext.domain.order.Order;
import com.mossy.boundedContext.out.order.OrderRepository;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
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