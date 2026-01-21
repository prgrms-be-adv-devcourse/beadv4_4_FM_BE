package backend.mossy.boundedContext.payout.domain.donation;

import backend.mossy.shared.market.dto.event.OrderItemDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 탄소 배출량 계산기
 */
@Component
public class CarbonCalculator {

    // 탄소 계수 (설정 파일에서 변경 가능, 기본값 1)
    private static BigDecimal CARBON_COEFFICIENT;

    @Value("${custom.donation.carbonCoefficient:1}")
    public void setCarbonCoefficient(BigDecimal coefficient) {
        CARBON_COEFFICIENT = coefficient;
    }

    /**
     * 탄소 배출량 계산
     * @param weight 무게 (kg)
     * @param distance 거리 (km)
     * @return 탄소 배출량 (kg)
     */
    public BigDecimal calculate(BigDecimal weight, BigDecimal distance) {
        if (weight == null || distance == null) {
            return BigDecimal.ZERO;
        }

        return weight
                .multiply(distance)
                .multiply(CARBON_COEFFICIENT);
    }

    /**
     * OrderItemDto로부터 탄소 배출량 계산
     */
    public BigDecimal calculate(OrderItemDto orderItem) {
        return calculate(orderItem.weight(), orderItem.deliveryDistance());
    }

    public BigDecimal getCarbonCoefficient() {
        return CARBON_COEFFICIENT;
    }
}
