package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.marketUser.domain.MarketPolicy;
import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.boundedContext.marketUser.out.MarketUserRepository;
import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.in.dto.request.OrderCreatedRequest;
import com.mossy.boundedContext.order.in.dto.response.OrderCreatedResponse;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.boundedContext.order.out.external.OrderFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mossy.boundedContext.order.out.external.dto.request.StockCheckRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final MarketUserRepository marketUserRepository;
    private final OrderRepository orderRepository;
    private final OrderFeignClient orderFeignClient;
    private final MarketPolicy marketPolicy;

    @Transactional
    public OrderCreatedResponse create(
            Long userId,
            OrderCreatedRequest request,
            Map<Long, MarketSeller> sellerMap,
            Map<Long, BigDecimal> couponDiscountMap
    ) {
        MarketUser buyer = marketUserRepository.getReferenceById(userId);

        List<StockCheckRequest> stockCheckRequests = request.items().stream()
                .map(item -> new StockCheckRequest(item.productItemId(), item.quantity()))
                .toList();

        orderFeignClient.validateStock(stockCheckRequests);

        String orderNo = marketPolicy.generateOrderNo();

        Order savedOrder = orderRepository.save(
                Order.create(
                        buyer,
                        request.buyerAddress(),
                        orderNo,
                        sellerMap,
                        request.items(),
                        request.totalPrice(),
                        couponDiscountMap
                )
        );

        return OrderCreatedResponse.builder()
                .orderId(savedOrder.getId())
                .orderNo(savedOrder.getOrderNo())
                .totalPrice(savedOrder.getTotalPrice())
                .build();
    }
}
