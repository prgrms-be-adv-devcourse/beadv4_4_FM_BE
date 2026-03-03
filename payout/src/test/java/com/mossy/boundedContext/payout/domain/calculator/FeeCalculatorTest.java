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
import static org.mockito.Mockito.mock;

class FeeCalculatorTest {

    private FeeCalculator feeCalculator;

    @BeforeEach
    void setUp() {
        // FeeCalculator는 CarbonCalculator를 생성자로 주입받으나,
        // calculate()는 CarbonCalculator를 사용하지 않으므로 mock으로 대체
        CarbonCalculator carbonCalculator = mock(CarbonCalculator.class);
        feeCalculator = new FeeCalculator(carbonCalculator);
    }

    // ===== 예외 케이스 =====

    @Test
    @DisplayName("null dto → DomainException")
    void null_dto_throws_exception() {
        assertThatThrownBy(() -> feeCalculator.calculate(null))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("orderPrice가 null → DomainException")
    void null_order_price_throws_exception() {
        PayoutCandidateCreateDto dto = PayoutCandidateCreateDto.builder()
                .orderPrice(null)
                .weightGrade("소형")
                .deliveryDistance(new BigDecimal("30"))
                .paymentDate(LocalDateTime.now())
                .build();
        assertThatThrownBy(() -> feeCalculator.calculate(dto))
                .isInstanceOf(DomainException.class);
    }

    // ===== 수수료 계산 (고정 20%) =====

    @Test
    @DisplayName("10,000원 → 2,000원 (20%)")
    void fee_is_20_percent_of_order_price() {
        assertThat(feeCalculator.calculate(createDto("10000")))
                .isEqualByComparingTo(new BigDecimal("2000"));
    }

    @Test
    @DisplayName("100,000원 → 20,000원")
    void fee_100000_returns_20000() {
        assertThat(feeCalculator.calculate(createDto("100000")))
                .isEqualByComparingTo(new BigDecimal("20000"));
    }

    @Test
    @DisplayName("1,000원 → 200원")
    void fee_1000_returns_200() {
        assertThat(feeCalculator.calculate(createDto("1000")))
                .isEqualByComparingTo(new BigDecimal("200"));
    }

    // ===== 반올림 검증 (HALF_UP) =====

    @Test
    @DisplayName("10,001원 × 20% = 2,000.2원 → 2,000원 (버림)")
    void fee_rounds_down_when_decimal_less_than_half() {
        // 10001 * 0.20 = 2000.2 → HALF_UP → 2000
        assertThat(feeCalculator.calculate(createDto("10001")))
                .isEqualByComparingTo(new BigDecimal("2000"));
    }

    @Test
    @DisplayName("10,003원 × 20% = 2,000.6원 → 2,001원 (올림)")
    void fee_rounds_up_when_decimal_half_or_more() {
        // 10003 * 0.20 = 2000.6 → HALF_UP → 2001
        assertThat(feeCalculator.calculate(createDto("10003")))
                .isEqualByComparingTo(new BigDecimal("2001"));
    }

    @Test
    @DisplayName("10,005원 × 20% = 2,001.0원 → 2,001원 (정확히 반)")
    void fee_at_exactly_half_rounds_up() {
        // 10005 * 0.20 = 2001.0 → HALF_UP → 2001
        assertThat(feeCalculator.calculate(createDto("10005")))
                .isEqualByComparingTo(new BigDecimal("2001"));
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
