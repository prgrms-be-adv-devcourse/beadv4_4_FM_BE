package backend.mossy.boundedContext.market.app.order;

import backend.mossy.boundedContext.market.domain.market.MarketPolicy;
import backend.mossy.boundedContext.market.domain.market.MarketSeller;
import backend.mossy.boundedContext.market.domain.market.MarketUser;
import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.domain.order.WeightGrade;
import backend.mossy.boundedContext.market.domain.order.WeightGradeType;
import backend.mossy.boundedContext.market.out.market.MarketSellerRepository;
import backend.mossy.boundedContext.market.out.market.MarketUserRepository;
import backend.mossy.boundedContext.market.out.order.OrderRepository;
import backend.mossy.boundedContext.market.out.order.WeightGradeRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.event.OrderDetailDto;
import backend.mossy.shared.market.event.PaymentCompletedEvent;
import backend.mossy.shared.market.out.ProductApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final MarketUserRepository marketUserRepository;
    private final MarketSellerRepository marketSellerRepository;
    private final OrderRepository orderRepository;
    private final WeightGradeRepository weightGradeRepository;
    private final ProductApiClient productClient;
    private final MarketPolicy marketPolicy;

    public void createOrder(PaymentCompletedEvent event) {
        MarketUser buyer = marketUserRepository.getReferenceById(event.order().buyerId());

        Order order = Order.create(buyer, event.order());

        for (OrderDetailDto dto : event.orderDetails()) {
            MarketSeller seller = marketSellerRepository.getReferenceById(dto.sellerId());

            // 무게 등급 계산
            BigDecimal weight = productClient.getWeight(dto.productId());
            BigDecimal totalWeight = weight.multiply(BigDecimal.valueOf(dto.quantity()));

            // 무게 등급 테이블 비교
            WeightGradeType gradeType = marketPolicy.getWeightGrade(totalWeight);
            WeightGrade weightGrade = weightGradeRepository.findByWeightGradeName(gradeType.name())
                    .orElseThrow(() -> new DomainException(ErrorCode.WEIGHT_GRADE_NOT_FOUND));

            // 주문 상세 생성
            order.addOrderDetail(seller, weightGrade, dto);
        }

        orderRepository.save(order);
    }
}
