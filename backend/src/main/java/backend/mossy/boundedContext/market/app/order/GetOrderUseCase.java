package backend.mossy.boundedContext.market.app.order;

import backend.mossy.boundedContext.market.out.order.OrderRepository;
import backend.mossy.shared.market.dto.response.OrderDetailResponse;
import backend.mossy.shared.market.dto.response.OrderListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetOrderUseCase {

    private final OrderRepository orderRepository;

    public Page<OrderListResponse> getOrderListByUserId(
            Long userId,
            Pageable pageable
    ) {
        return orderRepository.findOrderListByUserId(userId, pageable);
    }

    public List<OrderDetailResponse> getOrderDetails(Long orderId) {
        return orderRepository.findOrderDetailsByOrderId(orderId);
    }
}