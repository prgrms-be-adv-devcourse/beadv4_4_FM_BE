package backend.mossy.boundedContext.payout.app.donation;

import backend.mossy.boundedContext.payout.app.payout.PayoutSupport;
import backend.mossy.boundedContext.payout.domain.donation.DonationCalculator;
import backend.mossy.boundedContext.payout.domain.donation.DonationLog;
import backend.mossy.boundedContext.payout.domain.payout.PayoutUser;
import backend.mossy.boundedContext.payout.out.donation.DonationLogRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DonationCreateLogUseCaseTest {

    @InjectMocks
    private DonationCreateLogUseCase donationCreateLogUseCase;

    @Mock
    private DonationCalculator donationCalculator;

    @Mock
    private DonationLogRepository donationLogRepository;

    @Mock
    private PayoutSupport payoutSupport;

    @Test
    @DisplayName("성공: 모든 정보가 유효하면 기부 로그를 성공적으로 저장한다")
    void createDonationLog_Success() {
        // given
        OrderPayoutDto dto = createDefaultDto();
        PayoutUser mockUser = PayoutUser.builder().id(dto.buyerId()).build();

        given(payoutSupport.findUserById(dto.buyerId())).willReturn(Optional.of(mockUser));
        given(donationCalculator.calculate(dto)).willReturn(new BigDecimal("100"));
        given(donationCalculator.getCarbon(dto)).willReturn(new BigDecimal("0.125"));

        // when
        donationCreateLogUseCase.createDonationLog(dto);

        // then
        // 실제로 repository.save()가 호출되었는지 확인하는 것이 핵심!
        verify(donationLogRepository).save(any(DonationLog.class));
    }

    @Test
    @DisplayName("실패: 기부자(사용자)를 찾을 수 없으면 PAYOUT_USER_NOT_FOUND 예외를 던진다")
    void createDonationLog_Fail_UserNotFound() {
        // given
        OrderPayoutDto dto = createDefaultDto();
        given(payoutSupport.findUserById(dto.buyerId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> donationCreateLogUseCase.createDonationLog(dto))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYOUT_USER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패: 계산된 기부금이 음수이면 INVALID_DONATION_AMOUNT 예외를 던진다")
    void createDonationLog_Fail_InvalidAmount() {
        // given
        OrderPayoutDto dto = createDefaultDto();
        PayoutUser mockUser = PayoutUser.builder().id(dto.buyerId()).build();

        given(payoutSupport.findUserById(dto.buyerId())).willReturn(Optional.of(mockUser));
        given(donationCalculator.calculate(dto)).willReturn(new BigDecimal("-1")); // 음수 발생 상황

        // when & then
        assertThatThrownBy(() -> donationCreateLogUseCase.createDonationLog(dto))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_DONATION_AMOUNT);
    }

    private OrderPayoutDto createDefaultDto() {
        return OrderPayoutDto.builder()
                .id(1L)
                .buyerId(50L)
                .build();
    }
}