package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.coupon.domain.UserCoupon;
import com.mossy.boundedContext.marketUser.domain.MarketPolicy;
import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.boundedContext.marketUser.out.MarketUserRepository;
import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.in.dto.request.OrderCreatedRequest;
import com.mossy.boundedContext.order.in.dto.response.OrderCreatedResponse;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.boundedContext.order.out.external.OrderFeignClient;
import com.mossy.kafka.KafkaTopics;
import com.mossy.kafka.outbox.service.OutboxPublisher;
import com.mossy.shared.market.event.OrderStockReturnEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mossy.boundedContext.order.out.external.dto.request.StockCheckRequest;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final MarketUserRepository marketUserRepository;
    private final OrderRepository orderRepository;
    private final OrderFeignClient orderFeignClient;
    private final MarketPolicy marketPolicy;
    private final OutboxPublisher outboxPublisher;

    @Transactional
    public OrderCreatedResponse create(
            Long userId,
            OrderCreatedRequest request,
            Map<Long, UserCoupon> userCouponMap
    ) {
        MarketUser buyer = marketUserRepository.getReferenceById(userId);

        List<StockCheckRequest> stockCheckRequests = request.items().stream()
                .map(item -> new StockCheckRequest(item.productItemId(), item.quantity()))
                .toList();

        orderFeignClient.decreaseStock(stockCheckRequests);

        try {
            String orderNo = marketPolicy.generateOrderNo();

            Order savedOrder = orderRepository.save(
                    Order.create(
                            buyer,
                            request.buyerAddress(),
                            orderNo,
                            request.items(),
                            request.totalPrice(),
                            userCouponMap
                    )
            );

            return OrderCreatedResponse.builder()
                    .orderId(savedOrder.getId())
                    .orderNo(savedOrder.getOrderNo())
                    .totalPrice(savedOrder.getTotalPrice())
                    .build();

        } catch (Exception e) {
            List<OrderStockReturnEvent.OrderItemStock> returnItems = stockCheckRequests.stream()
                .map(item -> new OrderStockReturnEvent.OrderItemStock(
                    item.productItemId(),
                    item.quantity()
                ))
                .toList();

            outboxPublisher.saveCompensationEvent(
                KafkaTopics.ORDER_CREATE_FAILED,
                "Order",
                System.nanoTime(),
                OrderStockReturnEvent.class.getSimpleName(),
                new OrderStockReturnEvent(returnItems)
            );
            throw e;
        }
    }
}
