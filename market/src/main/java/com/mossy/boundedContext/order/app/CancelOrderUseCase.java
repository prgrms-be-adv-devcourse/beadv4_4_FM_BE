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
import com.mossy.shared.market.event.OrderStockReturnEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
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

        String cancelReason = extractCancelReason(response);

        List<OrderItem> canceledItems = processCancellation(order, response, cancelReason);

        publishCancelEvents(order, canceledItems);
    }

    private String extractCancelReason(TossCancelPayload response) {
        return response.cancels().isEmpty()
            ? "사용자 요청"
            : response.cancels().getFirst().cancelReason();
    }

    private List<OrderItem> processCancellation(Order order, TossCancelPayload response, String cancelReason) {
        if ("PARTIAL_CANCELED".equals(response.refundType())) {
            return processPartialCancellation(order, response, cancelReason);
        }
        return processFullCancellation(order, cancelReason);
    }

    private List<OrderItem> processPartialCancellation(Order order, TossCancelPayload response, String cancelReason) {
        order.cancelPartial(response.orderItemIds(), cancelReason);
        return order.getOrderItems().stream()
            .filter(item -> response.orderItemIds().contains(item.getId()))
            .toList();
    }

    private List<OrderItem> processFullCancellation(Order order, String cancelReason) {
        order.cancel(cancelReason);
        return order.getOrderItems();
    }

    private void publishCancelEvents(Order order, List<OrderItem> canceledItems) {
        restoreCoupons(order, canceledItems);
        returnStock(order, canceledItems);
    }

    private void restoreCoupons(Order order, List<OrderItem> canceledItems) {
        List<Long> userCouponIds = canceledItems.stream()
            .map(OrderItem::getUserCouponId)
            .filter(Objects::nonNull)
            .toList();

        if (!userCouponIds.isEmpty()) {
            eventPublisher.publish(new OrderCancelEvent(
                order.getId(),
                order.getBuyer().getId(),
                userCouponIds
            ));
        }
    }

    private void returnStock(Order order, List<OrderItem> canceledItems) {
        List<OrderStockReturnEvent.OrderItemStock> orderItemStocks = canceledItems.stream()
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
