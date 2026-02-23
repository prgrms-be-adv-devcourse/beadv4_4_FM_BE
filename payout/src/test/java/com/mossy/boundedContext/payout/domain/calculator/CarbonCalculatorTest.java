package com.mossy.boundedContext.payout.domain.calculator;

import com.mossy.boundedContext.payout.in.dto.command.PayoutCandidateCreateDto;
import com.mossy.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarbonCalculatorTest {

    private CarbonCalculator carbonCalculator;

    @BeforeEach
    void setUp() {
        carbonCalculator = new CarbonCalculator();
        // @Value가 주입하는 static 필드를 setter로 직접 설정
        carbonCalculator.setCarbonCoefficient(new BigDecimal("0.01"));
    }

    // ===== 예외 케이스 =====

    @Test
    @DisplayName("null dto → DomainException")
    void null_dto_throws_exception() {
        assertThatThrownBy(() -> carbonCalculator.calculate(null))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("null weightGrade → DomainException")
    void null_weight_grade_throws_exception() {
        PayoutCandidateCreateDto dto = createDto(null, "30");
        assertThatThrownBy(() -> carbonCalculator.calculate(dto))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("유효하지 않은 weightGrade → DomainException")
    void invalid_weight_grade_throws_exception() {
        PayoutCandidateCreateDto dto = createDto("초대형", "30");
        assertThatThrownBy(() -> carbonCalculator.calculate(dto))
                .isInstanceOf(DomainException.class);
    }

    // ===== 무게 대표값 검증 (거리 고정: 25km 근거리) =====

    @Test
    @DisplayName("소형(0.5kg) + 근거리 → 0.5 × 25 × 0.01 = 0.125")
    void small_weight_near_distance() {
        PayoutCandidateCreateDto dto = createDto("소형", "25");
        assertThat(carbonCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("0.125"));
    }

    @Test
    @DisplayName("중소형(3kg) + 근거리 → 3 × 25 × 0.01 = 0.75")
    void medium_small_weight_near_distance() {
        PayoutCandidateCreateDto dto = createDto("중소형", "25");
        assertThat(carbonCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("0.75"));
    }

    @Test
    @DisplayName("중형(7.5kg) + 근거리 → 7.5 × 25 × 0.01 = 1.875")
    void medium_weight_near_distance() {
        PayoutCandidateCreateDto dto = createDto("중형", "25");
        assertThat(carbonCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("1.875"));
    }

    @Test
    @DisplayName("대형(15kg) + 근거리 → 15 × 25 × 0.01 = 3.75")
    void large_weight_near_distance() {
        PayoutCandidateCreateDto dto = createDto("대형", "25");
        assertThat(carbonCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("3.75"));
    }

    // ===== 거리 구간 경계값 검증 (무게 고정: 소형) =====

    @Test
    @DisplayName("거리 50km(경계) → 대표값 25km 사용: 0.5 × 25 × 0.01 = 0.125")
    void distance_50km_boundary_uses_25km_representative() {
        PayoutCandidateCreateDto dto = createDto("소형", "50");
        assertThat(carbonCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("0.125"));
    }

    @Test
    @DisplayName("거리 51km(경계 초과) → 대표값 100km 사용: 0.5 × 100 × 0.01 = 0.5")
    void distance_51km_uses_100km_representative() {
        PayoutCandidateCreateDto dto = createDto("소형", "51");
        assertThat(carbonCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("0.5"));
    }

    @Test
    @DisplayName("거리 150km(경계) → 대표값 100km 사용: 0.5 × 100 × 0.01 = 0.5")
    void distance_150km_boundary_uses_100km_representative() {
        PayoutCandidateCreateDto dto = createDto("소형", "150");
        assertThat(carbonCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("0.5"));
    }

    @Test
    @DisplayName("거리 151km(경계 초과) → 대표값 225km 사용: 0.5 × 225 × 0.01 = 1.125")
    void distance_151km_uses_225km_representative() {
        PayoutCandidateCreateDto dto = createDto("소형", "151");
        assertThat(carbonCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("1.125"));
    }

    @Test
    @DisplayName("거리 300km(경계) → 대표값 225km 사용: 0.5 × 225 × 0.01 = 1.125")
    void distance_300km_boundary_uses_225km_representative() {
        PayoutCandidateCreateDto dto = createDto("소형", "300");
        assertThat(carbonCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("1.125"));
    }

    @Test
    @DisplayName("거리 301km(경계 초과, 도서/제주) → 대표값 400km 사용: 0.5 × 400 × 0.01 = 2.0")
    void distance_301km_uses_400km_representative() {
        PayoutCandidateCreateDto dto = createDto("소형", "301");
        assertThat(carbonCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("2.0"));
    }

    // ===== 최대 탄소 케이스 =====

    @Test
    @DisplayName("대형 + 도서/제주 → 15 × 400 × 0.01 = 60")
    void large_weight_island_distance_max_carbon() {
        PayoutCandidateCreateDto dto = createDto("대형", "400");
        assertThat(carbonCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("60"));
    }

    private PayoutCandidateCreateDto createDto(String weightGrade, String distance) {
        return PayoutCandidateCreateDto.builder()
                .weightGrade(weightGrade)
                .deliveryDistance(new BigDecimal(distance))
                .orderPrice(new BigDecimal("10000"))
                .paymentDate(LocalDateTime.now())
                .build();
    }
}
