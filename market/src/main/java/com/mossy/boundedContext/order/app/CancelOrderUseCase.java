package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.domain.OrderItem;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.boundedContext.order.in.dto.event.OrderCancelEvent;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.global.eventPublisher.EventPublisher;
import com.mossy.kafka.KafkaTopics;
import com.mossy.kafka.outbox.service.OutboxPublisher;
import com.mossy.shared.cash.payload.TossCancelPayload;
import com.mossy.shared.market.enums.OrderState;
import com.mossy.shared.market.event.OrderStockReturnEvent;
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
    private final OutboxPublisher outboxPublisher;

    @Transactional
    public void cancelOrder(TossCancelPayload response) {
        Order order = orderRepository.findByOrderNo(response.orderId())
            .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getState() != OrderState.PAID) {
            throw new DomainException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        if (order.getUpdatedAt().isBefore(LocalDateTime.now().minusWeeks(1))) {
            throw new DomainException(ErrorCode.ORDER_PURCHASE_CONFIRMED);
        }

        String cancelReason = response.cancels().isEmpty()
            ? "사용자 요청" : response.cancels().getFirst().cancelReason();

        order.cancel(cancelReason);

        List<Long> userCouponIds = order.getOrderItems().stream()
                .map(OrderItem::getUserCouponId)
                .filter(Objects::nonNull)
                .toList();

        // 쿠폰 복구를 위한 내부 이벤트 발행
        eventPublisher.publish(new OrderCancelEvent(
                order.getId(),
                order.getBuyer().getId(),
                userCouponIds
        ));

        // 재고 복구를 위한 이벤트를 아웃박스에 저장
        List<OrderStockReturnEvent.OrderItemStock> orderItemStocks = order.getOrderItems().stream()
                .map(orderItem -> new OrderStockReturnEvent.OrderItemStock(
                        orderItem.getProductItemId(),
                        orderItem.getQuantity()
                ))
                .toList();

        outboxPublisher.saveEvent(
                KafkaTopics.ORDER_STOCK_RETURN,
                "Order",
                order.getId(),
                OrderStockReturnEvent.class.getSimpleName(),
                new OrderStockReturnEvent(orderItemStocks)
        );
    }
}
