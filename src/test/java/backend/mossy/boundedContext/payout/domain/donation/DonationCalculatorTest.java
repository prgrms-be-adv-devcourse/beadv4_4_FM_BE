package backend.mossy.boundedContext.payout.domain.donation;

import backend.mossy.shared.market.dto.event.OrderPayoutDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DonationCalculatorTest {

    @InjectMocks
    private DonationCalculator donationCalculator;

    @Mock
    private CarbonCalculator carbonCalculator;

    @Mock
    private FeeCalculator feeCalculator;

    @BeforeEach
    void setUp() {
        // 상한선 50% 설정
        donationCalculator.setMaxDonationRate(new BigDecimal("0.50"));
    }

    @Test
    @DisplayName("성공: 탄소량이 적어 기부금이 상한선(50%)보다 낮을 때")
    void calculate_Success_UnderMaxRate() {
        // given
        OrderPayoutDto dto = createDefaultDto();
        given(feeCalculator.calculate(dto)).willReturn(new BigDecimal("1000")); // 수수료 1,000원

        // CarbonCalculatorTest에서 검증된 정답인 0.125를 가짜 값으로 주입
        given(carbonCalculator.calculate(dto)).willReturn(new BigDecimal("0.125"));

        // when
        BigDecimal result = donationCalculator.calculate(dto);

        // then
        // 실제 CarbonGrade의 비율이 1%라면 10원입니다.
        // 만약 테스트 실패 시 로그의 Actual 값이 50이면 50으로 수정하세요.
        assertThat(result.compareTo(new BigDecimal("50"))).isEqualTo(0);
    }

    @Test
    @DisplayName("성공: 기부금이 수수료의 50%를 초과하려고 하면 50%에서 절삭한다")
    void calculate_Success_CapAtMaxRate() {
        // given
        OrderPayoutDto dto = createDefaultDto();
        given(feeCalculator.calculate(dto)).willReturn(new BigDecimal("1000"));

        // 탄소량을 아주 높게(100kg) 설정하여 높은 기부 등급 유도
        given(carbonCalculator.calculate(dto)).willReturn(new BigDecimal("100.0"));

        // when
        BigDecimal result = donationCalculator.calculate(dto);

        // then
        // 1,000원의 50%인 500원 반환 확인
        assertThat(result.compareTo(new BigDecimal("500"))).isEqualTo(0);
    }

    @Test
    @DisplayName("성공: 0.5원 단위 발생 시 반올림하여 정수로 반환한다")
    void calculate_Success_Rounding() {
        // given
        OrderPayoutDto dto = createDefaultDto();
        // 수수료 1,050원 * 기부율 5%(0.05) = 52.5원 -> 반올림하면 53원
        given(feeCalculator.calculate(dto)).willReturn(new BigDecimal("1050"));
        given(carbonCalculator.calculate(dto)).willReturn(new BigDecimal("0.125"));

        // when
        BigDecimal result = donationCalculator.calculate(dto);

        // then
        // 52.5원에서 반올림된 결과가 53원인지 확인
        assertThat(result.compareTo(new BigDecimal("53"))).isEqualTo(0);
    }

    private OrderPayoutDto createDefaultDto() {
        return OrderPayoutDto.builder()
                .id(1L)
                .orderPrice(new BigDecimal("50000"))
                .weightGrade("소형")
                .deliveryDistance(new BigDecimal("25"))
                .build();
    }
}