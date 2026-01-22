package backend.mossy.boundedContext.payout.domain.donation;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * [Domain Model] 배송 거리를 레벨로 변환하는 Enum
 * <p>
 * 거리 범위 (km):
 * - LEVEL1: 0~50km → 대표값 50km (근거리)
 * - LEVEL2: 50~150km → 대표값 150km (중거리)
 * - LEVEL3: 150~300km → 대표값 300km (장거리)
 * - LEVEL4: 300km~ → 대표값 500km (도서/제주)
 */
@Getter
public enum DistanceLevel {
    LEVEL1("0", "50", "50"),
    LEVEL2("50", "150", "150"),
    LEVEL3("150", "300", "300"),
    LEVEL4("300", "99999", "500");

    private final BigDecimal minDistance;
    private final BigDecimal maxDistance;
    private final BigDecimal representativeValue;

    DistanceLevel(String min, String max, String representative) {
        this.minDistance = new BigDecimal(min);
        this.maxDistance = new BigDecimal(max);
        this.representativeValue = new BigDecimal(representative);
    }

    /**
     * 거리를 레벨로 변환
     *
     * @param distance 거리 (km)
     * @return 해당 레벨
     */
    public static DistanceLevel fromDistance(BigDecimal distance) {
        if (distance == null) return LEVEL1;

        return java.util.Arrays.stream(values())
                .filter(l -> distance.compareTo(l.minDistance) > 0 && distance.compareTo(l.maxDistance) <= 0)
                .findFirst()
                .orElse(LEVEL4);
    }
}
