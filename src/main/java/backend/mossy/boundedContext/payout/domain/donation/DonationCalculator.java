package backend.mossy.boundedContext.payout.domain.donation;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.event.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * [Domain Service] 주문 아이템(OrderItemDto)을 기반으로 기부금액 및 관련 탄소 배출량을 계산하는 도메인 서비스
 * 기부금 계산 로직과 탄소 배출량에 따른 등급 판정 로직을 캡슐화
 */
@Component
@RequiredArgsConstructor
public class DonationCalculator {

    private final CarbonCalculator carbonCalculator;
    private final FeeCalculator feeCalculator;

    // 최대 기부금 비율 (수수료의 50%)
    private static BigDecimal MAX_DONATION_RATE;

    /**
     * 외부 설정(application.yml 등)으로부터 최대 기부금 비율을 주입받는 setter 메서드
     * 이 비율은 수수료에 대한 최대 기부금액 제한을 설정하는 데 사용
     *
     * @param rate 설정 파일로부터 주입될 최대 기부금 비율 (기본값: 0.50)
     */
    @Value("${custom.donation.maxDonationRate:0.50}")
    public void setMaxDonationRate(BigDecimal rate) {
        MAX_DONATION_RATE = rate;
    }

    /**
     * 주어진 주문 아이템(OrderItemDto)에 대한 최종 기부금액을 계산
     * 계산 과정은 탄소 배출량 등급에 따른 비율 적용 및 최대 기부금 비율 제한을 포함
     *
     * @param orderItem 기부금 계산의 기준이 되는 주문 아이템 DTO
     * @return 계산된 기부금 (원 단위로 반올림됨)
     */
    public BigDecimal calculate(OrderItemDto orderItem) {
        // 1. 탄소 배출량 기반으로 수수료를 계산
        BigDecimal payoutFee = feeCalculator.calculate(orderItem);

        // 2. 주문 아이템의 배송 정보 등을 바탕으로 탄소 배출량(kg)을 계산
        BigDecimal carbon = carbonCalculator.calculate(orderItem);

        // 3. 계산된 탄소 배출량에 따라 탄소 등급(CarbonGrade)을 판정합니다.
        CarbonGrade grade = CarbonGrade.fromCarbon(carbon);

        // 4. 수수료에 탄소 등급별 기부 비율을 적용하여 기본 기부금을 계산
        //    원 단위로 반올림 처리
        BigDecimal calculatedDonation = payoutFee
                .multiply(grade.getDonationRate())
                .setScale(0, RoundingMode.HALF_UP); // 원단위 반올림

        // 5. 수수료에 '최대 기부금 비율'을 적용하여 기부금의 상한선을 계산
        //    이 또한 원 단위로 반올림 처리
        BigDecimal maxDonation = payoutFee
                .multiply(MAX_DONATION_RATE)
                .setScale(0, RoundingMode.HALF_UP);

        // 6. 계산된 기본 기부금과 최대 기부금 제한 중 더 작은 값을 최종 기부금으로 결정하여 반환
        //    이는 기부금이 특정 비율(예: 수수료의 50%)을 초과하지 않도록 보장
        return calculatedDonation.min(maxDonation);
    }

    /**
     * 주어진 주문 아이템(OrderItemDto)의 탄소 배출량을 기반으로 해당 아이템의 탄소 등급(CarbonGrade)을 조회
     *
     * @param orderItem 탄소 등급을 조회할 주문 아이템 DTO
     * @return 계산된 탄소 등급
     */
    public CarbonGrade getGrade(OrderItemDto orderItem) {
        BigDecimal carbon = carbonCalculator.calculate(orderItem);
        return CarbonGrade.fromCarbon(carbon);
    }

    /**
     * 주어진 주문 아이템(OrderItemDto)에 대한 총 탄소 배출량(kg 단위)을 계산하여 반환
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
        if (orderItem.payoutFee() == null) {
            throw new DomainException(ErrorCode.INVALID_PAYOUT_FEE);
        }
    }
}
