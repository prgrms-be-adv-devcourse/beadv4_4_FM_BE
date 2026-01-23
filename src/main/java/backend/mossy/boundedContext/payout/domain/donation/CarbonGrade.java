package backend.mossy.boundedContext.payout.domain.donation;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * [Domain Model] 탄소 배출량(Carbon)에 따른 등급을 정의하는 Enum 클래스
 * 각 등급은 특정 탄소 배출량 범위와 그에 상응하는 기부 비율을 가짐
 * 수수료는 주문금액의 20% 고정, 기부금은 수수료의 5%~50% (전체의 1%~10%)
 * DonationCalculator에서 기부금 계산 시 활용
 */
@Getter
public enum CarbonGrade {
    /**
     * 1등급 (S): 탄소 배출량 0~0.5kg, 수수료의 5% 기부 → 전체의 1% (최우수 친환경)
     */
    GRADE_1(new BigDecimal("0"),     new BigDecimal("0.5"),   new BigDecimal("0.05")),   // 0~0.5kg: 전체 1%
    /**
     * 2등급 (A): 탄소 배출량 0.5~1kg, 수수료의 10% 기부 → 전체의 2%
     */
    GRADE_2(new BigDecimal("0.5"),   new BigDecimal("1"),     new BigDecimal("0.10")),   // 0.5~1kg: 전체 2%
    /**
     * 3등급 (B): 탄소 배출량 1~2kg, 수수료의 15% 기부 → 전체의 3%
     */
    GRADE_3(new BigDecimal("1"),     new BigDecimal("2"),     new BigDecimal("0.15")),   // 1~2kg: 전체 3%
    /**
     * 4등급 (C): 탄소 배출량 2~5kg, 수수료의 20% 기부 → 전체의 4%
     */
    GRADE_4(new BigDecimal("2"),     new BigDecimal("5"),     new BigDecimal("0.20")),   // 2~5kg: 전체 4%
    /**
     * 5등급 (D): 탄소 배출량 5~10kg, 수수료의 25% 기부 → 전체의 5%
     */
    GRADE_5(new BigDecimal("5"),     new BigDecimal("10"),    new BigDecimal("0.25")),   // 5~10kg: 전체 5%
    /**
     * 6등급 (E): 탄소 배출량 10~15kg, 수수료의 30% 기부 → 전체의 6%
     */
    GRADE_6(new BigDecimal("10"),    new BigDecimal("15"),    new BigDecimal("0.30")),   // 10~15kg: 전체 6%
    /**
     * 7등급 (F): 탄소 배출량 15~25kg, 수수료의 35% 기부 → 전체의 7%
     */
    GRADE_7(new BigDecimal("15"),    new BigDecimal("25"),    new BigDecimal("0.35")),   // 15~25kg: 전체 7%
    /**
     * 8등급 (G): 탄소 배출량 25~40kg, 수수료의 40% 기부 → 전체의 8%
     */
    GRADE_8(new BigDecimal("25"),    new BigDecimal("40"),    new BigDecimal("0.40")),   // 25~40kg: 전체 8%
    /**
     * 9등급 (H): 탄소 배출량 40~60kg, 수수료의 45% 기부 → 전체의 9%
     */
    GRADE_9(new BigDecimal("40"),    new BigDecimal("60"),    new BigDecimal("0.45")),   // 40~60kg: 전체 9%
    /**
     * 10등급 (I): 탄소 배출량 60kg 초과, 수수료의 50% 기부 → 전체의 10% (탄소 배출 최다)
     */
    GRADE_10(new BigDecimal("60"),   new BigDecimal("99999"), new BigDecimal("0.50"));   // 60kg~: 전체 10%

    private final BigDecimal minCarbon;     // 해당 등급의 최소 탄소 배출량 (kg)
    private final BigDecimal maxCarbon;     // 해당 등급의 최대 탄소 배출량 (kg)
    private final BigDecimal donationRate;  // 해당 등급에 적용되는 수수료 대비 기부 비율 (5%~50%)

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
     * 만약 어떤 등급에도 해당하지 않으면 기본값으로 GRADE_10 등급을 반환 (maxCarbon이 매우 큰 GRADE_10 등급이므로 사실상 항상 매칭).
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
        return GRADE_10; // 모든 범위에 해당하지 않을 경우 GRADE_10 등급 반환 (실제 로직상 GRADE_10이 최대이므로 사실상 항상 매칭됨)
    }

}
