package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.donation.DonationCalculator;
import backend.mossy.boundedContext.payout.domain.donation.FeeCalculator;
import backend.mossy.boundedContext.payout.domain.payout.*;
import backend.mossy.boundedContext.payout.out.payout.PayoutCandidateItemRepository;
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
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayoutAddPayoutCandidateItemsUseCaseTest {

    @InjectMocks
    private PayoutAddPayoutCandidateItemsUseCase payoutAddPayoutCandidateItemsUseCase;

    @Mock
    private PayoutSupport payoutSupport;

    @Mock
    private PayoutCandidateItemRepository payoutCandidateItemRepository;

    @Mock
    private DonationCalculator donationCalculator;

    @Mock
    private FeeCalculator feeCalculator;

    @Test
    @DisplayName("성공: 하나의 주문에 대해 3개의 정산 후보 항목(수수료, 대금, 기부금)을 생성한다")
    void addPayoutCandidateItem_Success() {
        // given
        OrderPayoutDto dto = createDefaultDto();
        LocalDateTime paymentDate = LocalDateTime.now();

        // Actor 설정
        PayoutSeller system = mock(PayoutSeller.class);
        PayoutSeller donation = mock(PayoutSeller.class);
        PayoutSeller seller = mock(PayoutSeller.class);
        PayoutUser buyer = mock(PayoutUser.class);

        given(payoutSupport.findSystemSeller()).willReturn(Optional.of(system));
        given(payoutSupport.findDonationSeller()).willReturn(Optional.of(donation));
        given(payoutSupport.findUserById(dto.buyerId())).willReturn(Optional.of(buyer));
        given(payoutSupport.findSellerById(dto.sellerId())).willReturn(Optional.of(seller));

        // 금액 설정 (주문 10,000원 / 수수료 2,000원 / 기부금 500원)
        given(feeCalculator.calculate(dto)).willReturn(new BigDecimal("2000"));
        given(donationCalculator.calculate(dto)).willReturn(new BigDecimal("500"));

        // when
        payoutAddPayoutCandidateItemsUseCase.addPayoutCandidateItem(dto, paymentDate);

        // then
        // 3가지 항목(수수료, 대금, 기부금)이 각각 한 번씩 저장되었는지 확인
        verify(payoutCandidateItemRepository, times(3)).save(any(PayoutCandidateItem.class));
    }

    @Test
    @DisplayName("실패: 기부금이 수수료보다 크면 INVALID_PAYOUT_FEE 예외가 발생한다")
    void addPayoutCandidateItem_Fail_DonationOverFee() {
        // given
        OrderPayoutDto dto = createDefaultDto();

        // Actor 설정 생략 방지를 위해 최소한의 모킹
        given(payoutSupport.findSystemSeller()).willReturn(Optional.of(mock(PayoutSeller.class)));
        given(payoutSupport.findDonationSeller()).willReturn(Optional.of(mock(PayoutSeller.class)));
        given(payoutSupport.findUserById(any())).willReturn(Optional.of(mock(PayoutUser.class)));
        given(payoutSupport.findSellerById(any())).willReturn(Optional.of(mock(PayoutSeller.class)));

        // 수수료 1,000원인데 기부금이 1,500원인 말도 안되는 상황 가정
        given(feeCalculator.calculate(dto)).willReturn(new BigDecimal("1000"));
        given(donationCalculator.calculate(dto)).willReturn(new BigDecimal("1500"));

        // when & then
        assertThatThrownBy(() -> payoutAddPayoutCandidateItemsUseCase.addPayoutCandidateItem(dto, LocalDateTime.now()))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PAYOUT_FEE);
    }

    private OrderPayoutDto createDefaultDto() {
        return OrderPayoutDto.builder()
                .id(1L)
                .orderPrice(new BigDecimal("10000"))
                .buyerId(50L)
                .sellerId(10L)
                .build();
    }
}