package com.mossy.boundedContext.app.order;

import com.mossy.boundedContext.domain.order.Order;
import com.mossy.boundedContext.out.order.OrderRepository;
import com.mossy.global.exception.DomainException;
import com.mossy.global.exception.ErrorCode;
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