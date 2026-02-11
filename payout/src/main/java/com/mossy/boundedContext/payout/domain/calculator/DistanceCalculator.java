package com.mossy.boundedContext.payout.domain.calculator;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * [Utility] 두 지점 간의 거리를 계산하는 유틸리티 클래스
 * Haversine 공식을 사용하여 위도/경도 기반으로 km 단위의 거리를 계산
 */
@Component
public class DistanceCalculator {

    // 지구 반지름 (km)
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Haversine 공식을 사용하여 두 지점 간의 거리를 계산
     *
     * @param lat1 첫 번째 지점의 위도 (latitude)
     * @param lon1 첫 번째 지점의 경도 (longitude)
     * @param lat2 두 번째 지점의 위도 (latitude)
     * @param lon2 두 번째 지점의 경도 (longitude)
     * @return 두 지점 간의 거리 (km 단위, 소수점 둘째 자리까지)
     */
    public BigDecimal calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            // 위도/경도 정보가 없는 경우 기본값 반환 (근거리로 가정)
            return new BigDecimal("25.0");
        }

        // BigDecimal을 double로 변환
        double latitude1 = lat1.doubleValue();
        double longitude1 = lon1.doubleValue();
        double latitude2 = lat2.doubleValue();
        double longitude2 = lon2.doubleValue();

        // 위도와 경도를 라디안으로 변환
        double lat1Rad = Math.toRadians(latitude1);
        double lat2Rad = Math.toRadians(latitude2);
        double deltaLat = Math.toRadians(latitude2 - latitude1);
        double deltaLon = Math.toRadians(longitude2 - longitude1);

        // Haversine 공식
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;

        // double을 BigDecimal로 변환 (소수점 둘째 자리까지 반올림)
        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }
}
