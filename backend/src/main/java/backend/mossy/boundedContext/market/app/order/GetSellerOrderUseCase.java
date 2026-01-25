package backend.mossy.boundedContext.market.app.order;

import backend.mossy.boundedContext.market.out.order.OrderRepository;
import backend.mossy.shared.market.dto.response.OrderDetailSellerResponse;
import backend.mossy.shared.market.dto.response.OrderListSellerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSellerOrderUseCase {

    private final OrderRepository orderRepository;

    public Page<OrderListSellerResponse> getSellerOrderList(Long sellerId, Pageable pageable) {
        return orderRepository.findSellerOrderListBySellerId(sellerId, pageable);
    }

    public OrderDetailSellerResponse getSellerOrderDetail(Long orderDetailId) {
        return orderRepository.findSellerOrderDetailById(orderDetailId);
    }
}