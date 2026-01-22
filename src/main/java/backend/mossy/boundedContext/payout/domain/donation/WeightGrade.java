package backend.mossy.boundedContext.payout.domain.donation;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * [Domain Model] 무게를 등급으로 변환하는 Enum
 * <p>
 * 무게 범위 (kg):
 * - SMALL: 0~1kg → 등급 1
 * - MEDIUM_SMALL: 1~5kg → 등급 2
 * - MEDIUM: 5~10kg → 등급 3
 * - LARGE: 10kg~ → 등급 4
 */
@Getter
public enum WeightGrade {
    SMALL("0", "1", 1),
    MEDIUM_SMALL("1", "5", 2),
    MEDIUM("5", "10", 3),
    LARGE("10", "99999", 4);

    private final BigDecimal minWeight;
    private final BigDecimal maxWeight;
    private final int gradeValue;

    WeightGrade(String min, String max, int gradeValue) {
        this.minWeight = new BigDecimal(min);
        this.maxWeight = new BigDecimal(max);
        this.gradeValue = gradeValue;
    }

    /**
     * 무게를 등급으로 변환
     *
     * @param weight 무게 (kg)
     * @return 해당 등급
     */
    public static WeightGrade fromWeight(BigDecimal weight) {
        if (weight == null) return SMALL;

        return java.util.Arrays.stream(values())
                .filter(g -> weight.compareTo(g.minWeight) > 0 && weight.compareTo(g.maxWeight) <= 0)
                .findFirst()
                .orElse(LARGE);
    }
}
