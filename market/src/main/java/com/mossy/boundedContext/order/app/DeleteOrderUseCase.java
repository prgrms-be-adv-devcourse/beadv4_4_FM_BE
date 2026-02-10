package com.mossy.boundedContext.order.app;

import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.shared.market.enums.OrderState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteOrderUseCase {

    private final OrderRepository orderRepository;

    public void deleteOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getBuyer().getId().equals(userId)) {
            throw new DomainException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        if (order.getState() != OrderState.PENDING) {
            throw new DomainException(ErrorCode.ORDER_CANNOT_DELETE);
        }

        orderRepository.delete(order);
    }
}