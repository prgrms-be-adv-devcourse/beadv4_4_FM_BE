package backend.mossy.boundedContext.payout.domain.donation;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.event.OrderPayoutDto;
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
     * @param coefficient 설정 파일로부터 주입될 탄소 계수 (기본값: 0.01)
     */
    @Value("${custom.donation.carbonCoefficient:0.01}")
    public void setCarbonCoefficient(BigDecimal coefficient) {
        CARBON_COEFFICIENT = coefficient;
    }

    /**
     * 무게 등급명을 대표값으로 변환
     * - 소형: 0.5kg
     * - 중소형: 3kg
     * - 중형: 7.5kg
     * - 대형: 15kg
     */
    private BigDecimal getRepresentativeWeightByGrade(String weightGrade) {
        if (weightGrade == null) {
            throw new DomainException(ErrorCode.INVALID_CARBON_CALCULATION_INPUT);
        }

        return switch (weightGrade) {
            case "소형" -> new BigDecimal("0.5");
            case "중소형" -> new BigDecimal("3");
            case "중형" -> new BigDecimal("7.5");
            case "대형" -> new BigDecimal("15");
            default -> throw new DomainException(ErrorCode.INVALID_CARBON_CALCULATION_INPUT);
        };
    }

    /**
     * 거리를 등급별 대표값으로 변환
     * - level1: 근거리 (0~50km): 25km
     * - level2: 중거리 (50~150km): 100km
     * - level3: 장거리 (150~300km): 225km
     * - level4: 도서/제주 (300km 초과): 400km
     */
    private BigDecimal getRepresentativeDistance(BigDecimal distance) {
        if (distance.compareTo(new BigDecimal("50")) <= 0) {
            return new BigDecimal("25");
        } else if (distance.compareTo(new BigDecimal("150")) <= 0) {
            return new BigDecimal("100");
        } else if (distance.compareTo(new BigDecimal("300")) <= 0) {
            return new BigDecimal("225");
        } else {
            return new BigDecimal("400");
        }
    }

    /**
     * OrderPayoutDto로부터 무게 등급(weightGrade)과 배송 거리(deliveryDistance)를 추출하여 탄소 배출량을 계산
     * 무게 등급을 대표값으로 변환 후 계산
     *
     * @param orderItem 탄소 배출량을 계산할 주문 정산 DTO
     * @return 계산된 탄소 배출량 (kg 단위)
     */
    public BigDecimal calculate(OrderPayoutDto orderItem) {
        if (orderItem == null) {
            throw new DomainException(ErrorCode.INVALID_CARBON_CALCULATION_INPUT);
        }

        // 무게 등급명을 대표값으로 변환
        BigDecimal representativeWeight = getRepresentativeWeightByGrade(orderItem.weightGrade());

        // 거리를 등급별 대표값으로 변환
        BigDecimal representativeDistance = getRepresentativeDistance(orderItem.deliveryDistance());

        return representativeWeight
                .multiply(representativeDistance)
                .multiply(CARBON_COEFFICIENT);
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
