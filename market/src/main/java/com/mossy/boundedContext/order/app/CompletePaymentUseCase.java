package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.domain.OrderItem;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.kafka.publisher.KafkaEventPublisher;
import com.mossy.shared.market.event.CouponUseRequestedEvent;
import com.mossy.shared.market.event.OrderPaidEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompletePaymentUseCase {

    private final OrderRepository orderRepository;
    //private final EventPublisher eventPublisher;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public void completePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        order.completePayment();

        List<Long> userCouponIds = order.getOrderItems().stream()
                .map(OrderItem::getUserCouponId)
                .filter(Objects::nonNull)
                .toList();

        if (!userCouponIds.isEmpty()) {
            //eventPublisher.publish(new CouponUseRequestedEvent(userCouponIds));
            kafkaEventPublisher.publish(new CouponUseRequestedEvent(userCouponIds));
        }

        //eventPublisher.publish(new OrderPaidEvent(order.getBuyer().getId()));
        kafkaEventPublisher.publish(new OrderPaidEvent(order.getBuyer().getId()));
    }
}
