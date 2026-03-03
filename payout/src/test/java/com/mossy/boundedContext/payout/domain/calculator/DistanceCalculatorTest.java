package com.mossy.boundedContext.payout.domain.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DistanceCalculatorTest {

    private DistanceCalculator distanceCalculator;

    @BeforeEach
    void setUp() {
        distanceCalculator = new DistanceCalculator();
    }

    @Test
    @DisplayName("lat1이 null → 기본값 25.0 반환")
    void null_lat1_returns_default() {
        BigDecimal result = distanceCalculator.calculateDistance(
                null, new BigDecimal("126.9"), new BigDecimal("35.1"), new BigDecimal("129.0"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("25.0"));
    }

    @Test
    @DisplayName("lon1이 null → 기본값 25.0 반환")
    void null_lon1_returns_default() {
        BigDecimal result = distanceCalculator.calculateDistance(
                new BigDecimal("37.5"), null, new BigDecimal("35.1"), new BigDecimal("129.0"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("25.0"));
    }

    @Test
    @DisplayName("lat2이 null → 기본값 25.0 반환")
    void null_lat2_returns_default() {
        BigDecimal result = distanceCalculator.calculateDistance(
                new BigDecimal("37.5"), new BigDecimal("126.9"), null, new BigDecimal("129.0"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("25.0"));
    }

    @Test
    @DisplayName("lon2이 null → 기본값 25.0 반환")
    void null_lon2_returns_default() {
        BigDecimal result = distanceCalculator.calculateDistance(
                new BigDecimal("37.5"), new BigDecimal("126.9"), new BigDecimal("35.1"), null);
        assertThat(result).isEqualByComparingTo(new BigDecimal("25.0"));
    }

    @Test
    @DisplayName("모든 파라미터 null → 기본값 25.0 반환")
    void all_null_returns_default() {
        BigDecimal result = distanceCalculator.calculateDistance(null, null, null, null);
        assertThat(result).isEqualByComparingTo(new BigDecimal("25.0"));
    }

    @Test
    @DisplayName("동일 좌표 → 0.00km")
    void same_coordinates_returns_zero() {
        BigDecimal result = distanceCalculator.calculateDistance(
                new BigDecimal("37.5"), new BigDecimal("126.9"),
                new BigDecimal("37.5"), new BigDecimal("126.9"));
        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("서울-부산 직선거리 약 300~360km 범위")
    void seoul_to_busan_distance_in_expected_range() {
        // 서울: 37.5665, 126.9780 / 부산: 35.1796, 129.0756
        BigDecimal result = distanceCalculator.calculateDistance(
                new BigDecimal("37.5665"), new BigDecimal("126.9780"),
                new BigDecimal("35.1796"), new BigDecimal("129.0756"));
        assertThat(result).isBetween(new BigDecimal("300"), new BigDecimal("360"));
    }

    @Test
    @DisplayName("근거리 좌표 → 소수점 둘째 자리까지 반환")
    void result_has_two_decimal_places() {
        BigDecimal result = distanceCalculator.calculateDistance(
                new BigDecimal("37.5"), new BigDecimal("126.9"),
                new BigDecimal("37.6"), new BigDecimal("127.0"));
        assertThat(result.scale()).isEqualTo(2);
    }
}
