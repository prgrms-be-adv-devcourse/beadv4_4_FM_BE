package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.coupon.app.CouponFacade;
import com.mossy.boundedContext.marketUser.domain.MarketSeller;
import com.mossy.boundedContext.marketUser.out.MarketSellerRepository;
import com.mossy.boundedContext.order.in.dto.request.OrderCreatedRequest;
import com.mossy.boundedContext.order.in.dto.request.OrderCreatedRequest.OrderItemRequest;
import com.mossy.boundedContext.order.in.dto.response.*;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.cash.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private static final ConcurrentHashMap<Long, Boolean> processingOrders = new ConcurrentHashMap<>();

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetSellerOrderUseCase getSellerOrderUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final CompletePaymentUseCase completePaymentUseCase;
    private final MarketSellerRepository marketSellerRepository;
    private final CouponFacade couponFacade;

    public OrderCreatedResponse createOrder(Long userId, OrderCreatedRequest request) {
        if (processingOrders.putIfAbsent(userId, true) != null) {
            throw new DomainException(ErrorCode.DUPLICATE_ORDER_REQUEST);
        }

        try {
            Map<Long, MarketSeller> sellerMap = fetchSellerMap(request.items());
            Map<Long, BigDecimal> couponDiscountMap = couponFacade.calculateDiscounts(
                    buildUserCouponPriceMap(request.items())
            );
            return createOrderUseCase.create(userId, request, sellerMap, couponDiscountMap);
        } finally {
            processingOrders.remove(userId);
        }
    }

    public void completePayment(PaymentCompletedEvent event) {
        completePaymentUseCase.completePayment(event.orderId());
    }

    @Transactional(readOnly = true)
    public Page<OrderListResponse> getOrderListByUserId(Long userId, Pageable pageable) {
        return getOrderUseCase.getOrderListByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public List<OrderDetailResponse> getOrderDetails(Long orderId) {
        return getOrderUseCase.getOrderDetails(orderId);
    }

    @Transactional
    public void deleteOrder(Long orderId, Long userId) {
        deleteOrderUseCase.deleteOrder(orderId, userId);
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId, String cancelReason) {
        cancelOrderUseCase.cancelOrder(orderId, userId, cancelReason);
    }

    @Transactional(readOnly = true)
    public Page<OrderListSellerResponse> getSellerOrderList(Long sellerId, Pageable pageable) {
        return getSellerOrderUseCase.getSellerOrderList(sellerId, pageable);
    }

    @Transactional(readOnly = true)
    public OrderDetailSellerResponse getSellerOrderDetail(Long orderDetailId) {
        return getSellerOrderUseCase.getSellerOrderDetail(orderDetailId);
    }

    private Map<Long, MarketSeller> fetchSellerMap(List<OrderItemRequest> items) {
        Set<Long> sellerIds = items.stream()
                .map(OrderItemRequest::sellerId)
                .collect(Collectors.toSet());
        return marketSellerRepository.findAllById(sellerIds).stream()
                .collect(Collectors.toMap(MarketSeller::getId, seller -> seller));
    }

    private Map<Long, BigDecimal> buildUserCouponPriceMap(List<OrderItemRequest> items) {
        return items.stream()
            .filter(item -> item.userCouponId() != null)
            .collect(Collectors.toMap(
                    OrderItemRequest::userCouponId,
                    item -> item.price().multiply(BigDecimal.valueOf(item.quantity()))
            ));
    }
}
