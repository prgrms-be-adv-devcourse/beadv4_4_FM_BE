package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.domain.payout.PayoutSeller;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.boundedContext.payout.out.payout.PayoutSellerRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayoutCreatePayoutUseCaseTest {

    @InjectMocks
    private PayoutCreatePayoutUseCase payoutCreatePayoutUseCase;

    @Mock
    private PayoutRepository payoutRepository;

    @Mock
    private PayoutSellerRepository payoutSellerRepository;

    @Test
    @DisplayName("성공: 활성화된 정산서가 없으면 새로운 Payout을 생성하고 저장한다")
    void createPayout_Success_New() {
        // given
        Long payeeId = 1L;
        PayoutSeller payee = mock(PayoutSeller.class);

        given(payoutSellerRepository.findById(payeeId)).willReturn(Optional.of(payee));
        // 기존에 생성된 정산서가 없는 상황 (Optional.empty)
        given(payoutRepository.findByPayeeAndPayoutDateIsNull(payee)).willReturn(Optional.empty());

        // when
        payoutCreatePayoutUseCase.createPayout(payeeId);

        // then
        // 새로운 Payout이 저장소에 save() 되었는지 확인
        verify(payoutRepository, times(1)).save(any(Payout.class));
    }

    @Test
    @DisplayName("성공: 이미 활성화된 정산서가 있으면 새로 저장하지 않는다 (중복 생성 방지)")
    void createPayout_Success_AlreadyExists() {
        // given
        Long payeeId = 1L;
        PayoutSeller payee = mock(PayoutSeller.class);
        Payout existingPayout = mock(Payout.class);

        given(payoutSellerRepository.findById(payeeId)).willReturn(Optional.of(payee));
        // 이미 진행 중인 정산서가 존재하는 상황
        given(payoutRepository.findByPayeeAndPayoutDateIsNull(payee)).willReturn(Optional.of(existingPayout));

        // when
        payoutCreatePayoutUseCase.createPayout(payeeId);

        // then
        // save() 메서드가 한 번도 호출되지 않아야 함 (이미 있으니까)
        verify(payoutRepository, never()).save(any(Payout.class));
    }

    @Test
    @DisplayName("실패: 존재하지 않는 판매자 ID인 경우 SELLER_NOT_FOUND 예외가 발생한다")
    void createPayout_Fail_SellerNotFound() {
        // given
        Long payeeId = 999L;
        given(payoutSellerRepository.findById(payeeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> payoutCreatePayoutUseCase.createPayout(payeeId))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SELLER_NOT_FOUND);
    }

    @Test
    @DisplayName("실패: payeeId가 null로 들어오면 INVALID_PAYEE_ID 예외가 발생한다")
    void createPayout_Fail_NullId() {
        // when & then
        assertThatThrownBy(() -> payoutCreatePayoutUseCase.createPayout(null))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PAYEE_ID);
    }
}