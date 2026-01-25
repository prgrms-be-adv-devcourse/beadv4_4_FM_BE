package backend.mossy.shared.market.out;

import backend.mossy.boundedContext.market.out.order.OrderRepository;
import backend.mossy.shared.market.dto.event.OrderPayoutDto;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MarketApiClient {
    private final OrderRepository orderRepository;

    public List<OrderPayoutDto> getOrderItems(Long orderId) {
        // 1. 입력값 검증
        if (orderId == null) {
            throw new DomainException(ErrorCode.ORDER_ID_REQUIRED);
        }

        // 2. DB 조회
        List<OrderPayoutDto> items = orderRepository.findPayoutOrderByOrderId(orderId);

        // 3. 비즈니스 예외 처리
        // 주문 항목이 없다는 것은 정산 프로세스를 진행할 수 없음을 의미하므로 예외를 발생시킵니다.
        if (CollectionUtils.isEmpty(items)) {
            throw new DomainException(ErrorCode.ORDERITEM_IS_NULL);
            // 또는 상황에 따라 ErrorCode.ORDER_NOT_FOUND 사용
        }

        return items;
    }
}