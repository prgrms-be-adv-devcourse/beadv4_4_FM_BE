package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.exception.DomainException;
import com.mossy.boundedContext.exception.ErrorCode;
import com.mossy.boundedContext.marketUser.domain.MarketPolicy;
import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.boundedContext.marketUser.domain.MarketUser;
import com.mossy.boundedContext.marketUser.out.MarketSellerRepository;
import com.mossy.boundedContext.marketUser.out.MarketUserRepository;
import com.mossy.boundedContext.order.domain.Order;
import com.mossy.boundedContext.order.in.dto.request.OrderCreatedRequest;
import com.mossy.boundedContext.order.in.dto.response.OrderCreatedResponse;
import com.mossy.boundedContext.order.out.OrderRepository;
import com.mossy.boundedContext.product.in.dto.response.ProductInfoResponse;
import com.mossy.global.eventPublisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final MarketUserRepository marketUserRepository;
    private final MarketSellerRepository marketSellerRepository;
    private final OrderRepository orderRepository;
    private final MarketPolicy marketPolicy;

    public OrderCreatedResponse createOrder(Long userId, OrderCreatedRequest request) {
        MarketUser buyer = marketUserRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        String orderNo = marketPolicy.generateOrderNo();

        // 1. order 생성
        Order order = Order.create(buyer, orderNo);

        // 2. sellerId 추출
        List<Long> sellerIds = request.items().stream()
                .map(ProductInfoResponse::sellerId)
                .distinct()
                .toList();

        // 3. 판매자 조회
        Map<Long, MarketSeller> sellerMap = marketSellerRepository.findAllById(sellerIds).stream()
                .collect(Collectors.toMap(MarketSeller::getId, Function.identity()));

        // 6. OrderDetail 생성
        for (ProductInfoResponse item : request.items()) {
            MarketSeller seller = sellerMap.get(item.sellerId());
            order.addOrderDetail(seller, item.productId(), item.quantity(), item.price());
        }

        // 7. 저장
        Order savedOrder = orderRepository.save(order);

        return OrderCreatedResponse.builder()
                .orderId(savedOrder.getId())
                .orderNo(savedOrder.getOrderNo())
                .totalPrice(savedOrder.getTotalPrice())
                .build();
    }
}
