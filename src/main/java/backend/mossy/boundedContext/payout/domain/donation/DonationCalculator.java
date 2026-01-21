package backend.mossy.boundedContext.payout.domain.donation;

import backend.mossy.shared.market.dto.event.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 기부금 계산기
 */
@Component
@RequiredArgsConstructor
public class DonationCalculator {

    private final CarbonCalculator carbonCalculator;

    // 최대 기부금 비율 (수수료의 50%)
    private static BigDecimal MAX_DONATION_RATE;

    @Value("${custom.donation.maxDonationRate:0.50}")
    public void setMaxDonationRate(BigDecimal rate) {
        MAX_DONATION_RATE = rate;
    }

    /**
     * 기부금 계산
     * @param orderItem 주문 아이템
     * @return 기부금 (원 단위 반올림)
     */
    public BigDecimal calculate(OrderItemDto orderItem) {
        // 1. 탄소량 계산
        BigDecimal carbon = carbonCalculator.calculate(orderItem);

        // 2. 등급 판정
        CarbonGrade grade = CarbonGrade.fromCarbon(carbon);

        // 3. 기부금 계산 (수수료 × 등급별 비율)
        BigDecimal calculatedDonation = orderItem.payoutFee()
                .multiply(grade.getDonationRate())
                .setScale(0, RoundingMode.HALF_UP); // 원단위 반올림

        // 4. 최대 기부금 (수수료의 50%)
        BigDecimal maxDonation = orderItem.payoutFee()
                .multiply(MAX_DONATION_RATE)
                .setScale(0, RoundingMode.HALF_UP);

        // 5. 제한 적용 (둘 중 작은 값)
        return calculatedDonation.min(maxDonation);
    }

    /**
     * 탄소 등급 조회
     */
    public CarbonGrade getGrade(OrderItemDto orderItem) {
        BigDecimal carbon = carbonCalculator.calculate(orderItem);
        return CarbonGrade.fromCarbon(carbon);
    }

    /**
     * 탄소 배출량 조회
     */
    public BigDecimal getCarbon(OrderItemDto orderItem) {
        return carbonCalculator.calculate(orderItem);
    }
}
