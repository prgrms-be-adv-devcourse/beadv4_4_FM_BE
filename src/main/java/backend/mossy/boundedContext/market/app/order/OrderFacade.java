package backend.mossy.boundedContext.market.app.order;

import backend.mossy.shared.market.dto.request.OrderCreatedRequest;
import backend.mossy.shared.market.dto.response.OrderResponse;
import backend.mossy.shared.market.dto.response.OrderCreatedResponse;
import backend.mossy.shared.market.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderFacade {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;
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
    public OrderResponse getOrder(Long orderId) {
        return getOrderUseCase.getOrder(orderId);
    }

    @Transactional
    public void deleteOrder(Long orderId, Long userId) {
        deleteOrderUseCase.deleteOrder(orderId, userId);
    }
}