package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.domain.OrderItem;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.boundedContext.order.in.dto.event.OrderCompletedEvent;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.global.eventPublisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompletePaymentUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void completePayment(Long orderId, LocalDateTime paidAt) {
        Order order = orderRepository.findWithItemsAndCouponsById(orderId)
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        order.completePayment(paidAt);

        List<Long> userCouponIds = order.getOrderItems().stream()
                .map(OrderItem::getUserCouponId)
                .filter(Objects::nonNull)
                .toList();

        eventPublisher.publish(new OrderCompletedEvent(
                order.getId(),
                order.getBuyer().getId(),
                paidAt,
                userCouponIds
        ));
    }
}
