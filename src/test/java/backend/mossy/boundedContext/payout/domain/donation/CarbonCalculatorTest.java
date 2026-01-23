package backend.mossy.boundedContext.payout.domain.donation;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.event.OrderPayoutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CarbonCalculatorTest {
    private CarbonCalculator carbonCalculator;

    @BeforeEach
    void setUp() {
        carbonCalculator = new CarbonCalculator();
        // @Value 주입을 수동으로 수행 (테스트 환경에서는 스프링이 주입해주지 않으므로)
        carbonCalculator.setCarbonCoefficient(new BigDecimal("0.01"));
    }
    @ParameterizedTest
    @DisplayName("성공: 무게 등급과 거리에 따른 탄소 배출량 계산 검증")
    @CsvSource({
            "소형, 25, 0.125",   // 0.5(무게) * 25(거리) * 0.01 = 0.125
            "중소형, 100, 3.00",  // 3.0 * 100 * 0.01 = 3.0
            "중형, 225, 16.875", // 7.5 * 225 * 0.01 = 16.875
            "대형, 400, 60.00"   // 15.0 * 400 * 0.01 = 60.0
    })
    void calculate_Success(String weightGrade, BigDecimal distance, BigDecimal expected) {
        // given
        OrderPayoutDto dto = createOrderPayoutDto(weightGrade, distance);

        // when
        BigDecimal result = carbonCalculator.calculate(dto);

        // then
        // compareTo를 사용해야 BigDecimal의 소수점 자릿수 차이로 인한 실패를 방지할 수 있습니다.
        assertThat(result.compareTo(expected)).isEqualTo(0);
    }
    @Test
    @DisplayName("실패: 유효하지 않은 무게 등급이 들어오면 예외가 발생한다")
    void calculate_Fail_InvalidWeightGrade() {
        // given
        OrderPayoutDto dto = createOrderPayoutDto("초대형", new BigDecimal("100"));

        // when & then
        assertThatThrownBy(() -> carbonCalculator.calculate(dto))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CARBON_CALCULATION_INPUT);
    }
    @Test
    @DisplayName("실패: OrderPayoutDto가 null이면 예외가 발생한다")
    void calculate_Fail_NullDto() {
        // when & then
        assertThatThrownBy(() -> carbonCalculator.calculate(null))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CARBON_CALCULATION_INPUT);
    }
    // 테스트용 DTO 생성 도우미 메서드
    private OrderPayoutDto createOrderPayoutDto(String weightGrade, BigDecimal distance) {
        return OrderPayoutDto.builder()
                .id(1L)
                .orderId(100L)
                .buyerId(50L)
                .buyerName("테스트구매자")
                .sellerId(10L)
                .productId(500L)
                .orderPrice(new BigDecimal("50000"))
                .weightGrade(weightGrade)       // 테스트의 핵심 변수
                .deliveryDistance(distance)    // 테스트의 핵심 변수
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    /**
     * 기본값이 설정된 표준 DTO (단순 조회 테스트용)
     */
    private OrderPayoutDto createDefaultDto() {
        return createOrderPayoutDto("소형", new BigDecimal("25"));
    }
}
