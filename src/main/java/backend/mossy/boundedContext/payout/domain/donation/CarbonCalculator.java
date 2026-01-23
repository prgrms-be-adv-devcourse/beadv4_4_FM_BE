package backend.mossy.boundedContext.payout.domain.donation;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.event.OrderItemDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * [Domain Service] 상품의 무게와 배송 거리를 기반으로 탄소 배출량(kg)을 계산하는 도메인 서비스
 * 외부 설정 가능한 탄소 계수(CARBON_COEFFICIENT)를 사용하여 계산의 유연성을 제공
 */
@Component
public class CarbonCalculator {

    // 탄소 계수 (설정 파일에서 변경 가능하며, 기본값은 1. 무게(kg) * 거리(km) * 계수 = 탄소량(kg) 공식에 사용)
    private static BigDecimal CARBON_COEFFICIENT;

    /**
     * 외부 설정(application.yml 등)으로부터 탄소 계수(CARBON_COEFFICIENT)를 주입받는 setter 메서드
     * @param coefficient 설정 파일로부터 주입될 탄소 계수 (기본값: 1)
     */
    @Value("${custom.donation.carbonCoefficient:1}")
    public void setCarbonCoefficient(BigDecimal coefficient) {
        CARBON_COEFFICIENT = coefficient;
    }

    /**
     * 상품의 무게와 배송 거리를 이용하여 탄소 배출량을 계산
     * 탄소 배출량 = 무게(kg) * 거리(km) * 탄소 계수
     *
     * @param weight 상품의 무게 (kg)
     * @param distance 상품의 배송 거리 (km)
     * @return 계산된 탄소 배출량 (kg 단위), 입력값이 null인 경우 0을 반환
     */
    public BigDecimal calculate(BigDecimal weight, BigDecimal distance) {
        if (weight == null || distance == null) {
            throw new DomainException(ErrorCode.INVALID_CARBON_CALCULATION_INPUT);
        }

        return weight
                .multiply(distance)
                .multiply(CARBON_COEFFICIENT);
    }

    /**
     * OrderItemDto로부터 무게(weight)와 배송 거리(deliveryDistance)를 추출하여 탄소 배출량을 계산
     * 내부적으로 {@link #calculate(BigDecimal, BigDecimal)} 메서드를 호출
     *
     * @param orderItem 탄소 배출량을 계산할 주문 아이템 DTO
     * @return 계산된 탄소 배출량 (kg 단위)
     */
    public BigDecimal calculate(OrderItemDto orderItem) {
        if (orderItem == null) {
            throw new DomainException(ErrorCode.INVALID_CARBON_CALCULATION_INPUT);
        }
        return calculate(orderItem.weight(), orderItem.deliveryDistance());
    }

    /**
     * 현재 설정된 탄소 계수(CARBON_COEFFICIENT)를 반환
     *
     * @return 현재 탄소 계수
     */
    public BigDecimal getCarbonCoefficient() {
        return CARBON_COEFFICIENT;
    }
}
