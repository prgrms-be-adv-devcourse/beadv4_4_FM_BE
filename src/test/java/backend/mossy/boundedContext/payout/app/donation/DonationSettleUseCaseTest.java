package backend.mossy.boundedContext.payout.app.donation;

import backend.mossy.boundedContext.payout.app.payout.PayoutSupport;
import backend.mossy.boundedContext.payout.domain.donation.DonationLog;
import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.domain.payout.PayoutItem;
import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import backend.mossy.boundedContext.payout.out.donation.DonationLogRepository;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DonationSettleUseCaseTest {

    @InjectMocks
    private DonationSettleUseCase donationSettleUseCase;

    @Mock
    private PayoutRepository payoutRepository;

    @Mock
    private DonationLogRepository donationLogRepository;

    @Mock
    private PayoutSupport payoutSupport;

    @Test
    @DisplayName("성공: 정산 완료된 항목 중 기부 판매자의 항목만 찾아 정산 완료 처리한다")
    void settleDonationLogs_Success() {
        // given
        Long payoutId = 1L;
        Long donationSellerId = 999L;
        Long orderItemId = 100L;

        // 1. 기부 전용 판매자 모킹
        PayoutSeller donationSeller = mock(PayoutSeller.class);
        given(donationSeller.getId()).willReturn(donationSellerId);
        given(payoutSupport.findDonationSeller()).willReturn(Optional.of(donationSeller));

        // 2. Payout 및 PayoutItem 모킹 (기부 항목 1개 포함)
        PayoutItem donationItem = mock(PayoutItem.class);
        given(donationItem.getPayee()).willReturn(donationSeller);
        given(donationItem.getRelId()).willReturn(orderItemId);

        Payout payout = mock(Payout.class);
        given(payout.getItems()).willReturn(List.of(donationItem));
        given(payoutRepository.findById(payoutId)).willReturn(Optional.of(payout));

        // 3. DonationLog 모킹
        DonationLog donationLog = mock(DonationLog.class);
        given(donationLogRepository.findByOrderItemId(orderItemId)).willReturn(List.of(donationLog));

        // when
        donationSettleUseCase.settleDonationLogs(payoutId);

        // then
        // 상태 변경 메서드가 호출되었는지 검증 (DonationLog 내부에 settle() 메서드가 있어야 함)
        org.mockito.Mockito.verify(donationLog).settle();
    }

    @Test
    @DisplayName("실패: 정산 항목 중 기부금 관련 항목이 하나도 없으면 예외가 발생한다")
    void settleDonationLogs_Fail_NoDonationItems() {
        // given
        Long payoutId = 1L;
        PayoutSeller donationSeller = mock(PayoutSeller.class);
        given(donationSeller.getId()).willReturn(999L);
        given(payoutSupport.findDonationSeller()).willReturn(Optional.of(donationSeller));

        // 일반 판매자만 있는 항목 리스트
        PayoutSeller normalSeller = mock(PayoutSeller.class);
        given(normalSeller.getId()).willReturn(1L);
        PayoutItem normalItem = mock(PayoutItem.class);
        given(normalItem.getPayee()).willReturn(normalSeller);

        Payout payout = mock(Payout.class);
        given(payout.getItems()).willReturn(List.of(normalItem));
        given(payoutRepository.findById(payoutId)).willReturn(Optional.of(payout));

        // when & then
        assertThatThrownBy(() -> donationSettleUseCase.settleDonationLogs(payoutId))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DONATION_PAYOUT_ITEM_NOT_FOUND);
    }
}