package com.mossy.boundedContext.order.app;

import com.mossy.boundedContext.coupon.app.CouponFacade;
import com.mossy.boundedContext.coupon.dto.CouponDiscountInfo;
import com.mossy.boundedContext.order.in.dto.request.OrderCreatedRequest;
import com.mossy.boundedContext.order.in.dto.request.OrderCreatedRequest.OrderItemRequest;
import com.mossy.boundedContext.order.in.dto.response.*;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.cash.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final RedissonClient redissonClient;
    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetSellerOrderUseCase getSellerOrderUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final CompletePaymentUseCase completePaymentUseCase;
    private final CouponFacade couponFacade;

    public OrderCreatedResponse createOrder(Long userId, OrderCreatedRequest request) {
        String lockKey = "order:user:" + userId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 2초 대기, 5초 후 자동 해제
            boolean isLocked = lock.tryLock(2, 5, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new DomainException(ErrorCode.DUPLICATE_ORDER_REQUEST);
            }

            // 쿠폰 썼는지 검증
            boolean hasCoupon = request.items().stream()
                    .anyMatch(item -> item.userCouponId() != null);

            Map<Long, CouponDiscountInfo> couponInfoMap = hasCoupon
                    ? couponFacade.calculateDiscounts(buildUserCouponPriceMap(request.items()))
                    : Map.of();

            return createOrderUseCase.create(userId, request, couponInfoMap);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DomainException(ErrorCode.ORDER_CREATION_FAILED);
        } finally {
            // 락 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void completePayment(PaymentCompletedEvent event) {
        completePaymentUseCase.completePayment(event.orderId(), event.paymentDate());
    }

    public Page<OrderListResponse> getOrderListByUserId(Long userId, Pageable pageable) {
        return getOrderUseCase.getOrderListByUserId(userId, pageable);
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

    public Page<OrderListSellerResponse> getSellerOrderList(Long sellerId, Pageable pageable) {
        return getSellerOrderUseCase.getSellerOrderList(sellerId, pageable);
    }

    public OrderDetailSellerResponse getSellerOrderDetail(Long orderDetailId) {
        return getSellerOrderUseCase.getSellerOrderDetail(orderDetailId);
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
