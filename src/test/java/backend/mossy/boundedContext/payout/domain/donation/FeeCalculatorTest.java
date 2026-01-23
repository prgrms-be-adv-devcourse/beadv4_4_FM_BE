package backend.mossy.boundedContext.payout.domain.donation;

import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.shared.market.dto.event.OrderPayoutDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FeeCalculatorTest {

    @InjectMocks
    private FeeCalculator feeCalculator;

    @Mock
    private CarbonCalculator carbonCalculator;

    @Test
    @DisplayName("성공: 주문 금액의 20%를 수수료로 계산한다 (반올림 확인)")
    void calculate_Success() {
        // given: 10,000원 주문 시 20%는 2,000원
        OrderPayoutDto dto = createDtoWithPrice(new BigDecimal("10000"));

        // when
        BigDecimal result = feeCalculator.calculate(dto);

        // then
        assertThat(result.compareTo(new BigDecimal("2000"))).isEqualTo(0);
    }

    @Test
    @DisplayName("성공: 수수료 계산 시 원 단위에서 반올림된다")
    void calculate_Rounding_Success() {
        // given: 1,234원 주문 시 20%는 246.8원 -> 반올림하면 247원
        OrderPayoutDto dto = createDtoWithPrice(new BigDecimal("1234"));

        // when
        BigDecimal result = feeCalculator.calculate(dto);

        // then
        assertThat(result.compareTo(new BigDecimal("247"))).isEqualTo(0);
    }

    @Test
    @DisplayName("실패: 주문 아이템이 null이면 INVALID_DONATION_CALCULATION_INPUT 예외가 발생한다")
    void calculate_Fail_NullDto() {
        // when & then
        assertThatThrownBy(() -> feeCalculator.calculate(null))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_DONATION_CALCULATION_INPUT);
    }

    @Test
    @DisplayName("실패: 주문 금액이 null이면 INVALID_PAYOUT_FEE 예외가 발생한다")
    void calculate_Fail_NullPrice() {
        // given
        OrderPayoutDto dto = createDtoWithPrice(null);

        // when & then
        assertThatThrownBy(() -> feeCalculator.calculate(dto))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PAYOUT_FEE);
    }

    @Test
    @DisplayName("성공: 탄소 배출량을 정확히 조회한다")
    void getCarbon_Success() {
        // given
        OrderPayoutDto dto = createDtoWithPrice(new BigDecimal("10000"));
        given(carbonCalculator.calculate(dto)).willReturn(new BigDecimal("1.55"));

        // when
        BigDecimal result = feeCalculator.getCarbon(dto);

        // then
        assertThat(result).isEqualTo(new BigDecimal("1.55"));
    }

    // -------------------------------------------------------------------------
    // 테스트 도우미 메서드
    // -------------------------------------------------------------------------
    private OrderPayoutDto createDtoWithPrice(BigDecimal price) {
        return OrderPayoutDto.builder()
                .id(1L)
                .orderPrice(price)
                .weightGrade("소형")
                .deliveryDistance(new BigDecimal("25"))
                .build();
    }
}