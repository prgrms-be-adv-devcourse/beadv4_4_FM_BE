package backend.mossy.boundedContext.market.app.order;

import backend.mossy.boundedContext.market.domain.market.MarketPolicy;
import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.boundedContext.market.domain.market.MarketUser;
import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.out.market.MarketSellerRepository;
import backend.mossy.boundedContext.market.out.market.MarketUserRepository;
import backend.mossy.boundedContext.market.out.order.OrderRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.request.OrderCreatedRequest;
import backend.mossy.shared.market.dto.response.OrderCreatedResponse;
import backend.mossy.shared.market.dto.response.ProductInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        String address = buyer.getAddress();
        String orderNo = marketPolicy.generateOrderNo();

        Order order = Order.create(buyer, orderNo, address);

        for (ProductInfoResponse item : request.items()) {
            MarketSeller seller = marketSellerRepository.getReferenceById(item.sellerId());
            order.addOrderDetail(seller, item.productId(), item.quantity(), item.price());
        }

        Order savedOrder = orderRepository.save(order);

        return OrderCreatedResponse.builder()
                .orderId(savedOrder.getId())
                .orderNo(savedOrder.getOrderNo())
                .totalPrice(savedOrder.getTotalPrice())
                .build();
    }
}