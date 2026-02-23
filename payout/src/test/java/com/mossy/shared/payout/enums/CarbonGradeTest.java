package com.mossy.shared.payout.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CarbonGradeTest {

    @Test
    @DisplayName("0kg → GRADE_1")
    void carbon_0kg_returns_grade1() {
        assertThat(CarbonGrade.fromCarbon(new BigDecimal("0"))).isEqualTo(CarbonGrade.GRADE_1);
    }

    @Test
    @DisplayName("0.25kg → GRADE_1 (범위 내)")
    void carbon_0_25kg_returns_grade1() {
        assertThat(CarbonGrade.fromCarbon(new BigDecimal("0.25"))).isEqualTo(CarbonGrade.GRADE_1);
    }

    @ParameterizedTest(name = "{0}kg → {1}")
    @CsvSource({
            "0.5, GRADE_2",
            "1,   GRADE_3",
            "2,   GRADE_4",
            "5,   GRADE_5",
            "10,  GRADE_6",
            "15,  GRADE_7",
            "25,  GRADE_8",
            "40,  GRADE_9",
            "60,  GRADE_10"
    })
    @DisplayName("각 등급 하한 경계값 테스트")
    void boundary_lower_values(String carbon, String expectedGrade) {
        CarbonGrade result = CarbonGrade.fromCarbon(new BigDecimal(carbon.trim()));
        assertThat(result).isEqualTo(CarbonGrade.valueOf(expectedGrade.trim()));
    }

    @Test
    @DisplayName("매우 큰 탄소량 → GRADE_10")
    void very_large_carbon_returns_grade10() {
        assertThat(CarbonGrade.fromCarbon(new BigDecimal("9999"))).isEqualTo(CarbonGrade.GRADE_10);
    }

    @Test
    @DisplayName("GRADE_1 기부율 5%")
    void grade1_donation_rate_is_5_percent() {
        assertThat(CarbonGrade.GRADE_1.getDonationRate()).isEqualByComparingTo(new BigDecimal("0.05"));
    }

    @Test
    @DisplayName("GRADE_10 기부율 50%")
    void grade10_donation_rate_is_50_percent() {
        assertThat(CarbonGrade.GRADE_10.getDonationRate()).isEqualByComparingTo(new BigDecimal("0.50"));
    }

    @Test
    @DisplayName("등급이 높을수록 기부율도 높음 (단조증가)")
    void higher_grade_has_higher_donation_rate() {
        CarbonGrade[] grades = CarbonGrade.values();
        for (int i = 0; i < grades.length - 1; i++) {
            assertThat(grades[i].getDonationRate())
                    .isLessThan(grades[i + 1].getDonationRate());
        }
    }
}
