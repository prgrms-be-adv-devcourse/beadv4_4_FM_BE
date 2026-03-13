package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.order.domain.Order;
import com.mossy.kafka.KafkaTopics;
import com.mossy.kafka.outbox.service.OutboxPublisher;
import com.mossy.shared.market.event.OrderStockReturnEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpireOrderUseCase {

    private final OutboxPublisher outboxPublisher;

    @Transactional
    public void execute(Order order) {
        order.expire();

        List<OrderStockReturnEvent.StockItem> orderItemStocks = order.getOrderItems().stream()
                .map(orderItem -> new OrderStockReturnEvent.StockItem(
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

        log.info("주문 만료 처리 완료 - orderId: {}, 재고 복구 이벤트 저장", order.getId());
    }
}
