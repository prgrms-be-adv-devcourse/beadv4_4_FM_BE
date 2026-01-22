package backend.mossy.boundedContext.payout.domain.donation;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * [Domain Model] 배송 거리를 레벨로 변환하는 Enum 클래스
 * 실제 거리(km)를 레벨별 대표값으로 매핑하여 탄소 배출량 계산에 사용
 */
@Getter
public enum DistanceLevel {
    /**
     * Level 1 (근거리): 0km 초과 50km 이하 → 대표값 50km
     */
    LEVEL1(new BigDecimal("0"), new BigDecimal("50"), new BigDecimal("50")),

    /**
     * Level 2 (중거리): 50km 초과 150km 이하 → 대표값 150km
     */
    LEVEL2(new BigDecimal("50"), new BigDecimal("150"), new BigDecimal("150")),

    /**
     * Level 3 (장거리): 150km 초과 300km 이하 → 대표값 300km
     */
    LEVEL3(new BigDecimal("150"), new BigDecimal("300"), new BigDecimal("300")),

    /**
     * Level 4 (도서/제주): 300km 초과 → 대표값 500km
     */
    LEVEL4(new BigDecimal("300"), new BigDecimal("99999"), new BigDecimal("500"));

    private final BigDecimal minDistance;        // 최소 거리 (km)
    private final BigDecimal maxDistance;        // 최대 거리 (km)
    private final BigDecimal representativeValue; // 레벨 대표값 (km)

    /**
     * DistanceLevel 생성자
     *
     * @param minDistance 최소 거리
     * @param maxDistance 최대 거리
     * @param representativeValue 레벨 대표값
     */
    DistanceLevel(BigDecimal minDistance, BigDecimal maxDistance, BigDecimal representativeValue) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.representativeValue = representativeValue;
    }

    /**
     * 주어진 거리(km)에 해당하는 거리 레벨을 판정하여 반환
     *
     * @param distance 판정할 거리 (km)
     * @return 해당 거리에 맞는 DistanceLevel
     */
    public static DistanceLevel fromDistance(BigDecimal distance) {
        if (distance == null) {
            return LEVEL1;
        }

        for (DistanceLevel level : values()) {
            if (distance.compareTo(level.minDistance) > 0 &&    // distance > minDistance
                distance.compareTo(level.maxDistance) <= 0) {   // distance <= maxDistance
                return level;
            }
        }
        return LEVEL4; // 범위를 벗어나면 최대 레벨로 처리
    }
}
