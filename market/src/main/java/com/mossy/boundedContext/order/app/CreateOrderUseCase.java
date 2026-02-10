package com.mossy.boundedContext.order.app;

import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
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
import com.mossy.shared.market.enums.OrderState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final MarketUserRepository marketUserRepository;
    private final MarketSellerRepository marketSellerRepository;
    private final OrderRepository orderRepository;
    private final MarketPolicy marketPolicy;

    @Transactional
    public OrderCreatedResponse createOrder(Long userId, OrderCreatedRequest request) {
        MarketUser buyer = marketUserRepository.findByIdWithLock(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.BUYER_NOT_FOUND));

        orderRepository.findByBuyerIdAndState(userId, OrderState.PENDING)
                .ifPresent(Order::expire);

        // TODO: 재고 조회

        String orderNo = marketPolicy.generateOrderNo();

        Set<Long> sellerIds = request.items().stream()
                .map(ProductInfoResponse::sellerId)
                .collect(Collectors.toSet());

        Map<Long, MarketSeller> sellerMap = marketSellerRepository.findAllById(sellerIds).stream()
                .collect(Collectors.toMap(MarketSeller::getId, seller -> seller));

        Order savedOrder = orderRepository.save(
                Order.create(
                        buyer,
                        request.buyerAddress(),
                        orderNo,
                        sellerMap,
                        request.items(),
                        request.totalPrice()
                )
        );

        return OrderCreatedResponse.builder()
                .orderId(savedOrder.getId())
                .orderNo(savedOrder.getOrderNo())
                .totalPrice(savedOrder.getTotalPrice())
                .build();
    }
}
