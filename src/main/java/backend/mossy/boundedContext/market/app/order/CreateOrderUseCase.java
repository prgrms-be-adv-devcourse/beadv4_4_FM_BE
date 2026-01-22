package backend.mossy.boundedContext.market.app.order;

import backend.mossy.boundedContext.market.domain.market.MarketPolicy;
import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.boundedContext.market.domain.market.MarketUser;
import backend.mossy.boundedContext.market.domain.order.DeliveryDistance;
import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.domain.order.WeightGrade;
import backend.mossy.boundedContext.market.out.market.MarketSellerRepository;
import backend.mossy.boundedContext.market.out.market.MarketUserRepository;
import backend.mossy.boundedContext.market.out.order.DeliveryDistanceRepository;
import backend.mossy.boundedContext.market.out.order.OrderRepository;
import backend.mossy.boundedContext.market.out.order.WeightGradeRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.request.OrderCreatedRequest;
import backend.mossy.shared.market.dto.response.OrderCreatedResponse;
import backend.mossy.shared.market.dto.response.ProductInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final MarketUserRepository marketUserRepository;
    private final MarketSellerRepository marketSellerRepository;
    private final OrderRepository orderRepository;
    private final DeliveryDistanceRepository deliveryDistanceRepository;
    private final WeightGradeRepository weightGradeRepository;
    private final MarketPolicy marketPolicy;

    public OrderCreatedResponse createOrder(Long userId, OrderCreatedRequest request) {
        MarketUser buyer = marketUserRepository.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCode.USER_NOT_FOUND));

        String orderNo = marketPolicy.generateOrderNo();

        // 1. order 생성
        Order order = Order.create(buyer, orderNo);

        // 2. sellerId 추출
        List<Long> sellerIds = request.items().stream()
                .map(ProductInfoResponse::sellerId)
                .distinct()
                .toList();

        // 3. 판매자 조회
        Map<Long, MarketSeller> sellerMap = marketSellerRepository.findAllById(sellerIds).stream()
                .collect(Collectors.toMap(MarketSeller::getId, Function.identity()));

        // 4. 배송거리 계산
        List<DeliveryDistance> deliveryDistanceList = deliveryDistanceRepository.findAllByOrderByDistanceAsc();
        Map<Long, DeliveryDistance> deliveryMap = new HashMap<>();

        for (MarketSeller seller : sellerMap.values()) {
            DeliveryDistance deliveryDistance = DeliveryDistance.resolve(
                    deliveryDistanceList,
                    request.buyerLatitude(), request.buyerLongitude(),
                    seller.getLatitude(), seller.getLongitude()
            );
            deliveryMap.put(seller.getId(), deliveryDistance);
        }

        // 5. 무게등급 조건 조회
        List<WeightGrade> weightGrades = weightGradeRepository.findAllByOrderByMaxWeightAsc();

        // 6. OrderDetail 생성 (배송거리 + 무게등급)
        for (ProductInfoResponse item : request.items()) {
            MarketSeller seller = sellerMap.get(item.sellerId());
            DeliveryDistance deliveryDistance = deliveryMap.get(item.sellerId());
            BigDecimal totalWeight = item.weight().multiply(BigDecimal.valueOf(item.quantity()));
            WeightGrade weightGrade = WeightGrade.findByWeight(weightGrades, totalWeight);
            order.addOrderDetail(seller, item.productId(), item.quantity(), item.price(), deliveryDistance, weightGrade);
        }

        // 7. 저장
        Order savedOrder = orderRepository.save(order);

        return OrderCreatedResponse.builder()
                .orderId(savedOrder.getId())
                .orderNo(savedOrder.getOrderNo())
                .totalPrice(savedOrder.getTotalPrice())
                .build();
    }
}
