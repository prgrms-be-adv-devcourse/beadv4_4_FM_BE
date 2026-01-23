package backend.mossy.boundedContext.payout.domain.donation;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.event.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * [Domain Service] 주문 아이템의 수수료를 계산하는 도메인 서비스
 * 수수료는 주문금액의 20% 고정
 */
@Component
@RequiredArgsConstructor
public class FeeCalculator {

    private final CarbonCalculator carbonCalculator;

    // 고정 수수료율 (20%)
    private static final BigDecimal FEE_RATE = new BigDecimal("0.20");

    /**
     * 주어진 주문 아이템에 대한 수수료를 계산
     * 수수료 = 주문금액 × 20% (고정)
     *
     * @param orderItem 수수료 계산의 기준이 되는 주문 아이템 DTO
     * @return 계산된 수수료 (원 단위로 반올림됨)
     */
    public BigDecimal calculate(OrderItemDto orderItem) {
        validateOrderItem(orderItem);

        // 주문금액에 고정 수수료율(20%)을 적용하여 수수료 계산
        // 원 단위로 반올림 처리
        return orderItem.salePrice()
                .multiply(FEE_RATE)
                .setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 주어진 주문 아이템의 탄소 등급을 조회
     *
     * @param orderItem 탄소 등급을 조회할 주문 아이템 DTO
     * @return 계산된 탄소 등급
     */
    public CarbonGrade getGrade(OrderItemDto orderItem) {
        BigDecimal carbon = carbonCalculator.calculate(orderItem);
        return CarbonGrade.fromCarbon(carbon);
    }

    /**
     * 주어진 주문 아이템에 대한 총 탄소 배출량(kg 단위)을 계산하여 반환
     *
     * @param orderItem 탄소 배출량을 계산할 주문 아이템 DTO
     * @return 계산된 탄소 배출량 (kg 단위)
     */
    public BigDecimal getCarbon(OrderItemDto orderItem) {
        return carbonCalculator.calculate(orderItem);
    }

    private void validateOrderItem(OrderItemDto orderItem) {
        if (orderItem == null) {
            throw new DomainException(ErrorCode.INVALID_DONATION_CALCULATION_INPUT);
        }
        if (orderItem.salePrice() == null) {
            throw new DomainException(ErrorCode.INVALID_PAYOUT_FEE);
        }
    }
}
