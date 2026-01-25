package backend.mossy.boundedContext.market.app.order;

import backend.mossy.shared.market.dto.request.OrderCreatedRequest;
import backend.mossy.shared.market.dto.response.*;
import backend.mossy.shared.market.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final GetSellerOrderUseCase getSellerOrderUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final CompletePaymentUseCase completePaymentUseCase;

    @Transactional
    public OrderCreatedResponse createOrder(Long userId, OrderCreatedRequest request) {
        return createOrderUseCase.createOrder(userId, request);
    }

    @Transactional
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
}