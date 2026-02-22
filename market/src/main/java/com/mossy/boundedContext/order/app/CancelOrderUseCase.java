package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.domain.OrderItem;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.boundedContext.order.in.dto.event.OrderCancelEvent;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.shared.market.enums.OrderState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void cancelOrder(Long orderId, Long userId, String cancelReason) {
        Order order = orderRepository.findWithItemsById(orderId)
            .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getBuyer().getId().equals(userId)) {
            throw new DomainException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        if (order.getState() != OrderState.PAID) {
            throw new DomainException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        if (order.getUpdatedAt().isBefore(LocalDateTime.now().minusWeeks(1))) {
            throw new DomainException(ErrorCode.ORDER_PURCHASE_CONFIRMED);
        }

        order.cancel(cancelReason);

        orderRepository.save(order);

        List<Long> userCouponIds = order.getOrderItems().stream()
                .map(OrderItem::getUserCouponId)
                .filter(Objects::nonNull)
                .toList();

        eventPublisher.publish(new OrderCancelEvent(
                order.getId(),
                order.getBuyer().getId(),
                userCouponIds
        ));
    }
}
