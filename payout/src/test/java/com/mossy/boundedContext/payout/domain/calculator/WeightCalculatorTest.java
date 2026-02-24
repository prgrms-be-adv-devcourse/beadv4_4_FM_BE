package com.mossy.boundedContext.payout.domain.calculator;

import com.mossy.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeightCalculatorTest {

    private WeightCalculator weightCalculator;

    @BeforeEach
    void setUp() {
        weightCalculator = new WeightCalculator();
    }

    @Test
    @DisplayName("null 무게 → DomainException")
    void null_weight_throws_exception() {
        assertThatThrownBy(() -> weightCalculator.determineWeightGrade(null))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("0kg → DomainException")
    void zero_weight_throws_exception() {
        assertThatThrownBy(() -> weightCalculator.determineWeightGrade(BigDecimal.ZERO))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("음수 무게 → DomainException")
    void negative_weight_throws_exception() {
        assertThatThrownBy(() -> weightCalculator.determineWeightGrade(new BigDecimal("-1")))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("0.5kg → 소형")
    void weight_0_5kg_returns_small() {
        assertThat(weightCalculator.determineWeightGrade(new BigDecimal("0.5"))).isEqualTo("소형");
    }

    @Test
    @DisplayName("1kg → 소형 (경계값)")
    void weight_1kg_boundary_returns_small() {
        assertThat(weightCalculator.determineWeightGrade(new BigDecimal("1"))).isEqualTo("소형");
    }

    @Test
    @DisplayName("1.001kg → 중소형 (소형 경계 초과)")
    void weight_just_over_1kg_returns_medium_small() {
        assertThat(weightCalculator.determineWeightGrade(new BigDecimal("1.001"))).isEqualTo("중소형");
    }

    @Test
    @DisplayName("3kg → 중소형")
    void weight_3kg_returns_medium_small() {
        assertThat(weightCalculator.determineWeightGrade(new BigDecimal("3"))).isEqualTo("중소형");
    }

    @Test
    @DisplayName("5kg → 중소형 (경계값)")
    void weight_5kg_boundary_returns_medium_small() {
        assertThat(weightCalculator.determineWeightGrade(new BigDecimal("5"))).isEqualTo("중소형");
    }

    @Test
    @DisplayName("5.001kg → 중형 (중소형 경계 초과)")
    void weight_just_over_5kg_returns_medium() {
        assertThat(weightCalculator.determineWeightGrade(new BigDecimal("5.001"))).isEqualTo("중형");
    }

    @Test
    @DisplayName("7.5kg → 중형")
    void weight_7_5kg_returns_medium() {
        assertThat(weightCalculator.determineWeightGrade(new BigDecimal("7.5"))).isEqualTo("중형");
    }

    @Test
    @DisplayName("10kg → 중형 (경계값)")
    void weight_10kg_boundary_returns_medium() {
        assertThat(weightCalculator.determineWeightGrade(new BigDecimal("10"))).isEqualTo("중형");
    }

    @Test
    @DisplayName("10.001kg → 대형 (중형 경계 초과)")
    void weight_just_over_10kg_returns_large() {
        assertThat(weightCalculator.determineWeightGrade(new BigDecimal("10.001"))).isEqualTo("대형");
    }

    @Test
    @DisplayName("20kg → 대형")
    void weight_20kg_returns_large() {
        assertThat(weightCalculator.determineWeightGrade(new BigDecimal("20"))).isEqualTo("대형");
    }
}
