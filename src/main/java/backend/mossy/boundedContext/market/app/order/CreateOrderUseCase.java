package backend.mossy.boundedContext.market.app.order;

import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.boundedContext.market.domain.market.MarketUser;
import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.domain.order.WeightGrade;
import backend.mossy.boundedContext.market.out.market.MarketSellerRepository;
import backend.mossy.boundedContext.market.out.market.MarketUserRepository;
import backend.mossy.boundedContext.market.out.order.OrderRepository;
import backend.mossy.boundedContext.market.out.order.WeightGradeRepository;
import backend.mossy.shared.market.dto.event.OrderDetailDto;
import backend.mossy.shared.market.event.PaymentCompletedEvent;
import backend.mossy.shared.market.out.ProductApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final MarketUserRepository marketUserRepository;
    private final MarketSellerRepository marketSellerRepository;
    private final OrderRepository orderRepository;
    private final WeightGradeRepository weightGradeRepository;
    private final ProductApiClient productClient;

    public void createOrder(PaymentCompletedEvent event) {
        MarketUser buyer = marketUserRepository.getReferenceById(event.order().buyerId());
        Order order = Order.create(buyer, event.order());

        addOrderDetails(order, event.orderDetails());

        orderRepository.save(order);
    }

    private void addOrderDetails(Order order, List<OrderDetailDto> orderDetails) {
        // key:productId, value:weight
        // getTotalWeight에서 조회할 때 Map으로 조회 시 더 빠름
        // List로 받을 경우 productId 개수 만큼 계속 순회해야 함
        Map<Long, BigDecimal> weightMap = getWeightMap(orderDetails);
        List<WeightGrade> grades = weightGradeRepository.findAllByOrderByMaxWeightAsc();

        // 구매한 상품의 종류에 따라 주문 상세가 여러개가 생성되기 때문에 for문 처리
        for (OrderDetailDto dto : orderDetails) {
            MarketSeller seller = marketSellerRepository.getReferenceById(dto.sellerId());

            // 무게등급 처리
            BigDecimal totalWeight = getTotalWeight(dto, weightMap);
            WeightGrade weightGrade = WeightGrade.findByWeight(grades, totalWeight);

            order.addOrderDetail(seller, weightGrade, dto);
        }
    }

    private Map<Long, BigDecimal> getWeightMap(List<OrderDetailDto> orderDetails) {
        List<Long> productIds = orderDetails.stream()
                .map(OrderDetailDto::productId)
                .toList();
        return productClient.getWeights(productIds);
    }

    private BigDecimal getTotalWeight(OrderDetailDto dto, Map<Long, BigDecimal> weightMap) {
        BigDecimal weight = weightMap.get(dto.productId());
        return weight.multiply(BigDecimal.valueOf(dto.quantity()));
    }
}
