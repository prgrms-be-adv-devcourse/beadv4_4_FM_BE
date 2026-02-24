package com.mossy.boundedContext.payout.domain.calculator;

import com.mossy.boundedContext.payout.in.dto.command.PayoutCandidateCreateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonationCalculatorTest {

    @Mock
    private CarbonCalculator carbonCalculator;

    @Mock
    private FeeCalculator feeCalculator;

    private DonationCalculator donationCalculator;

    @BeforeEach
    void setUp() {
        donationCalculator = new DonationCalculator(carbonCalculator, feeCalculator);
        // @Value가 주입하는 static 필드를 setter로 직접 설정
        donationCalculator.setMaxDonationRate(new BigDecimal("0.50"));
    }

    // ===== 탄소 등급별 기부율 검증 =====

    @Test
    @DisplayName("GRADE_1(탄소 0.25kg): fee×5% < max(fee×50%) → fee×5% 반환")
    void grade1_donation_is_5_percent_of_fee() {
        PayoutCandidateCreateDto dto = createDto("10000");
        when(carbonCalculator.calculate(dto)).thenReturn(new BigDecimal("0.25")); // GRADE_1
        when(feeCalculator.calculate(dto)).thenReturn(new BigDecimal("2000"));

        // 2000 × 0.05 = 100, max = 2000 × 0.50 = 1000 → min(100, 1000) = 100
        assertThat(donationCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("100"));
    }

    @Test
    @DisplayName("GRADE_4(탄소 3kg): fee×20% < max → fee×20% 반환")
    void grade4_donation_is_20_percent_of_fee() {
        PayoutCandidateCreateDto dto = createDto("10000");
        when(carbonCalculator.calculate(dto)).thenReturn(new BigDecimal("3")); // GRADE_4
        when(feeCalculator.calculate(dto)).thenReturn(new BigDecimal("2000"));

        // 2000 × 0.20 = 400, max = 1000 → min(400, 1000) = 400
        assertThat(donationCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("400"));
    }

    @Test
    @DisplayName("GRADE_10(탄소 60kg): fee×50% = max(fee×50%) → fee×50% 반환")
    void grade10_donation_equals_max_limit() {
        PayoutCandidateCreateDto dto = createDto("10000");
        when(carbonCalculator.calculate(dto)).thenReturn(new BigDecimal("60")); // GRADE_10
        when(feeCalculator.calculate(dto)).thenReturn(new BigDecimal("2000"));

        // 2000 × 0.50 = 1000, max = 2000 × 0.50 = 1000 → min(1000, 1000) = 1000
        assertThat(donationCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("1000"));
    }

    // ===== 최대 기부금 제한(50%) 검증 =====

    @Test
    @DisplayName("계산된 기부금이 max(fee×50%)를 초과하면 max로 제한")
    void donation_capped_at_max_donation_rate() {
        // maxDonationRate를 낮게 설정하여 cap이 발동되는 케이스 시뮬레이션
        donationCalculator.setMaxDonationRate(new BigDecimal("0.10")); // max = fee × 10%

        PayoutCandidateCreateDto dto = createDto("10000");
        when(carbonCalculator.calculate(dto)).thenReturn(new BigDecimal("60")); // GRADE_10 → 50%
        when(feeCalculator.calculate(dto)).thenReturn(new BigDecimal("2000"));

        // 2000 × 0.50 = 1000, max = 2000 × 0.10 = 200 → min(1000, 200) = 200
        assertThat(donationCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("200"));
    }

    // ===== 반올림 검증 (HALF_UP) =====

    @Test
    @DisplayName("기부금 소수점 HALF_UP 반올림 확인")
    void donation_rounds_half_up() {
        PayoutCandidateCreateDto dto = createDto("10000");
        when(carbonCalculator.calculate(dto)).thenReturn(new BigDecimal("0.25")); // GRADE_1 → 5%
        when(feeCalculator.calculate(dto)).thenReturn(new BigDecimal("2010"));

        // 2010 × 0.05 = 100.5 → HALF_UP → 101, max = 2010 × 0.50 = 1005 → min(101, 1005) = 101
        assertThat(donationCalculator.calculate(dto)).isEqualByComparingTo(new BigDecimal("101"));
    }

    // ===== getGrade, getCarbon 위임 검증 =====

    @Test
    @DisplayName("getCarbon은 CarbonCalculator에 위임")
    void get_carbon_delegates_to_carbon_calculator() {
        PayoutCandidateCreateDto dto = createDto("10000");
        when(carbonCalculator.calculate(dto)).thenReturn(new BigDecimal("5"));

        assertThat(donationCalculator.getCarbon(dto)).isEqualByComparingTo(new BigDecimal("5"));
    }

    private PayoutCandidateCreateDto createDto(String price) {
        return PayoutCandidateCreateDto.builder()
                .orderPrice(new BigDecimal(price))
                .weightGrade("소형")
                .deliveryDistance(new BigDecimal("30"))
                .paymentDate(LocalDateTime.now())
                .build();
    }
}
