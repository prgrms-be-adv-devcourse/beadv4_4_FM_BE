package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.coupon.app.CouponFacade;
import com.mossy.boundedContext.coupon.domain.UserCoupon;
import com.mossy.boundedContext.order.in.dto.request.OrderCreatedRequest;
import com.mossy.boundedContext.order.in.dto.request.OrderCreatedRequest.OrderItemRequest;
import com.mossy.boundedContext.order.in.dto.response.OrderCreatedResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderDetailResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListResponse;
import com.mossy.boundedContext.order.in.dto.response.OrderListSellerResponse;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.cash.event.PaymentCompletedEvent;
import com.mossy.shared.market.enums.OrderState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final StringRedisTemplate redisTemplate;
    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetSellerOrderUseCase getSellerOrderUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final CompletePaymentUseCase completePaymentUseCase;
    private final CouponFacade couponFacade;

    public OrderCreatedResponse createOrder(Long userId, OrderCreatedRequest request) {
        String key = "order:prevent:" + userId;

        // 중복 요청 방지 (5초 동안 같은 사용자의 주문 생성 차단)
        Boolean isFirstRequest = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", 5, TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(isFirstRequest)) {
            throw new DomainException(ErrorCode.DUPLICATE_ORDER_REQUEST);
        }

        // 쿠폰 사용 여부 확인
        List<Long> userCouponIds = request.items().stream()
                .map(OrderItemRequest::userCouponId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, UserCoupon> userCouponMap =
                !userCouponIds.isEmpty()
                ? couponFacade.getUserCoupons(userCouponIds)
                : Map.of();

        return createOrderUseCase.create(userId, request, userCouponMap);
    }

    public void completePayment(PaymentCompletedEvent event) {
        completePaymentUseCase.completePayment(event.orderId(), event.paymentDate());
    }

    public Page<OrderListResponse> getOrderListByUserId(Long userId, OrderState state, java.time.LocalDate startDate, java.time.LocalDate endDate, Pageable pageable) {
        return getOrderUseCase.getOrderListByUserId(userId, state, startDate, endDate, pageable);
    }

    public List<OrderDetailResponse> getOrderDetails(Long orderId) {
        return getOrderUseCase.getOrderDetails(orderId);
    }

    public void deleteOrder(Long orderId, Long userId) {
        deleteOrderUseCase.deleteOrder(orderId, userId);
    }

    public void cancelOrder(Long orderId, Long userId, String cancelReason) {
        cancelOrderUseCase.cancelOrder(orderId, userId, cancelReason);
    }

    public Page<OrderListSellerResponse> getSellerOrderList(Long sellerId, OrderState state, Pageable pageable) {
        return getSellerOrderUseCase.getSellerOrderList(sellerId, state, pageable);
    }
}
