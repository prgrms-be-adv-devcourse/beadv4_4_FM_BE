package backend.mossy.boundedContext.market.app.order;

import backend.mossy.boundedContext.market.domain.order.Order;
import backend.mossy.boundedContext.market.domain.order.WeightGrade;
import backend.mossy.boundedContext.market.out.order.OrderRepository;
import backend.mossy.boundedContext.market.out.order.WeightGradeRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.out.ProductApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompletePaymentUseCase {

    private final OrderRepository orderRepository;
    private final WeightGradeRepository weightGradeRepository;
    private final ProductApiClient productClient;

    public void completePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new DomainException(ErrorCode.ORDER_NOT_FOUND));

        List<Long> productIds = order.getProductIds();
        Map<Long, BigDecimal> weightMap = productClient.getWeights(productIds);
        List<WeightGrade> grades = weightGradeRepository.findAllByOrderByMaxWeightAsc();

        order.calculateWeightGrades(weightMap, grades);
        order.completePayment();
    }
}