package backend.mossy.boundedContext.payout.domain.donation;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * [Domain Model] 탄소 배출량(Carbon)에 따른 등급을 정의하는 Enum 클래스
 * 각 등급은 특정 탄소 배출량 범위와 그에 상응하는 기부 비율을 가짐
 * DonationCalculator에서 기부금 계산 시 활용
 */
@Getter
public enum CarbonGrade {
    /**
     * A 등급: 탄소 배출량 0kg 초과 10kg 이하, 수수료의 10% 기부
     */
    A(new BigDecimal("0"),   new BigDecimal("10"),  new BigDecimal("0.10")),  // 0~10kg: 10%
    /**
     * B 등급: 탄소 배출량 10kg 초과 30kg 이하, 수수료의 20% 기부
     */
    B(new BigDecimal("10"),  new BigDecimal("30"),  new BigDecimal("0.20")),  // 10~30kg: 20%
    /**
     * C 등급: 탄소 배출량 30kg 초과 50kg 이하, 수수료의 30% 기부
     */
    C(new BigDecimal("30"),  new BigDecimal("50"),  new BigDecimal("0.30")),  // 30~50kg: 30%
    /**
     * D 등급: 탄소 배출량 50kg 초과, 수수료의 40% 기부 (최고 등급)
     */
    D(new BigDecimal("50"),  new BigDecimal("99999"), new BigDecimal("0.40")); // 50kg~: 40%

    private final BigDecimal minCarbon;     // 해당 등급의 최소 탄소 배출량 (kg)
    private final BigDecimal maxCarbon;     // 해당 등급의 최대 탄소 배출량 (kg)
    private final BigDecimal donationRate;  // 해당 등급에 적용되는 수수료 대비 기부 비율

    /**
     * CarbonGrade Enum의 생성자입니다.
     *
     * @param minCarbon    최소 탄소 배출량
     * @param maxCarbon    최대 탄소 배출량
     * @param donationRate 수수료 대비 기부 비율
     */
    CarbonGrade(BigDecimal minCarbon, BigDecimal maxCarbon, BigDecimal donationRate) {
        this.minCarbon = minCarbon;
        this.maxCarbon = maxCarbon;
        this.donationRate = donationRate;
    }

    /**
     * 주어진 탄소 배출량(kg)에 해당하는 CarbonGrade를 판정하여 반환
     * 탄소량이 각 등급의 [minCarbon, maxCarbon) 범위에 속하는지 확인
     * 만약 어떤 등급에도 해당하지 않으면 기본값으로 D 등급을 반환 (maxCarbon이 매우 큰 D 등급이므로 사실상 항상 매칭).
     *
     * @param carbon 판정할 탄소 배출량 (kg)
     * @return 해당 탄소량에 맞는 CarbonGrade
     */
    public static CarbonGrade fromCarbon(BigDecimal carbon) {
        for (CarbonGrade grade : values()) {
            if (carbon.compareTo(grade.minCarbon) >= 0 &&    // carbon >= minCarbon
                carbon.compareTo(grade.maxCarbon) < 0) {     // carbon < maxCarbon
                return grade;
            }
        }
        return D; // 모든 범위에 해당하지 않을 경우 D등급 반환 (실제 로직상 D가 최대이므로 항상 매칭됨)
    }
}
