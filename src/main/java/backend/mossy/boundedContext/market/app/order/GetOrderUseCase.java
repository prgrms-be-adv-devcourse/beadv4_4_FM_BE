package backend.mossy.boundedContext.market.app.order;

import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.out.order.OrderRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetOrderUseCase {

    private final OrderRepository orderRepository;

    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        return OrderResponse.of(order);
    }
}