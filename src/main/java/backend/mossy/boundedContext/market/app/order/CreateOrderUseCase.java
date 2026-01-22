package backend.mossy.boundedContext.market.app.order;

import backend.mossy.boundedContext.market.domain.market.MarketPolicy;
import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.boundedContext.market.domain.market.MarketUser;
import backend.mossy.boundedContext.market.domain.order.DeliveryDistance;
import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.out.market.MarketSellerRepository;
import backend.mossy.boundedContext.market.out.market.MarketUserRepository;
import backend.mossy.boundedContext.market.out.order.DeliveryDistanceRepository;
import backend.mossy.boundedContext.market.out.order.OrderRepository;
import backend.mossy.global.eventPublisher.EventPublisher;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.event.OrderCreateDto;
import backend.mossy.shared.market.dto.request.OrderCreatedRequest;
import backend.mossy.shared.market.dto.response.OrderCreatedResponse;
import backend.mossy.shared.market.dto.response.ProductInfoResponse;
import backend.mossy.shared.market.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final MarketUserRepository marketUserRepository;
    private final MarketSellerRepository marketSellerRepository;
    private final OrderRepository orderRepository;
    private final DeliveryDistanceRepository deliveryDistanceRepository;
    private final MarketPolicy marketPolicy;
    private final EventPublisher eventPublisher;

    public OrderCreatedResponse createOrder(Long userId, OrderCreatedRequest request) {
        MarketUser buyer = marketUserRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        String orderNo = marketPolicy.generateOrderNo();

        Order order = Order.create(buyer, orderNo);

        List<DeliveryDistance> deliveryDistances = deliveryDistanceRepository.findAllByOrderByDistanceAsc();

        for (ProductInfoResponse item : request.items()) {
            MarketSeller seller = marketSellerRepository.findById(item.sellerId())
                    .orElseThrow(() -> new DomainException(ErrorCode.SELLER_NOT_FOUND));

            int distance = DeliveryDistance.calculateDistance(
                    request.buyerLatitude(), request.buyerLongitude(),
                    seller.getLatitude(), seller.getLongitude()
            );

            DeliveryDistance deliveryDistance = DeliveryDistance.findByDistance(deliveryDistances, distance);

            order.addOrderDetail(seller, item.productId(), item.quantity(), item.price(), deliveryDistance);
        }

        Order savedOrder = orderRepository.save(order);

        eventPublisher.publish(new OrderCreatedEvent(
                new OrderCreateDto(
                        order.getId(),
                        orderNo,
                        order.getTotalPrice(),
                        request.paymentType()
                )
        ));

        return OrderCreatedResponse.builder()
                .orderId(savedOrder.getId())
                .orderNo(savedOrder.getOrderNo())
                .totalPrice(savedOrder.getTotalPrice())
                .build();
    }
}
