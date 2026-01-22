package backend.mossy.boundedContext.payout.domain.donation;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * [Domain Model] 무게를 등급으로 변환하는 Enum 클래스
 * 실제 무게(kg)를 등급(1~4)으로 매핑하여 탄소 배출량 계산에 사용
 */
@Getter
public enum WeightGrade {
    /**
     * 소형: 0kg 초과 1kg 이하 → 등급 1
     */
    SMALL(new BigDecimal("0"), new BigDecimal("1"), 1),

    /**
     * 중소형: 1kg 초과 5kg 이하 → 등급 2
     */
    MEDIUM_SMALL(new BigDecimal("1"), new BigDecimal("5"), 2),

    /**
     * 중형: 5kg 초과 10kg 이하 → 등급 3
     */
    MEDIUM(new BigDecimal("5"), new BigDecimal("10"), 3),

    /**
     * 대형: 10kg 초과 → 등급 4
     */
    LARGE(new BigDecimal("10"), new BigDecimal("99999"), 4);

    private final BigDecimal minWeight;  // 최소 무게 (kg)
    private final BigDecimal maxWeight;  // 최대 무게 (kg)
    private final int gradeValue;        // 등급 값 (1~4)

    /**
     * WeightGrade 생성자
     *
     * @param minWeight 최소 무게
     * @param maxWeight 최대 무게
     * @param gradeValue 등급 값
     */
    WeightGrade(BigDecimal minWeight, BigDecimal maxWeight, int gradeValue) {
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.gradeValue = gradeValue;
    }

    /**
     * 주어진 무게(kg)에 해당하는 무게 등급을 판정하여 반환
     *
     * @param weight 판정할 무게 (kg)
     * @return 해당 무게에 맞는 WeightGrade
     */
    public static WeightGrade fromWeight(BigDecimal weight) {
        if (weight == null) {
            return SMALL;
        }

        for (WeightGrade grade : values()) {
            if (weight.compareTo(grade.minWeight) > 0 &&    // weight > minWeight
                weight.compareTo(grade.maxWeight) <= 0) {   // weight <= maxWeight
                return grade;
            }
        }
        return LARGE; // 범위를 벗어나면 대형으로 처리
    }
}
