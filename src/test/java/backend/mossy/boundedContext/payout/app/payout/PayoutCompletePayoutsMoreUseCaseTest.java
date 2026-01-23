package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.exception.ErrorCode;
import backend.mossy.global.rsData.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayoutCompletePayoutsMoreUseCaseTest {

    @InjectMocks
    private PayoutCompletePayoutsMoreUseCase payoutCompletePayoutsMoreUseCase;

    @Mock
    private PayoutRepository payoutRepository;

    @Test
    @DisplayName("성공: 활성화된 정산건들을 모두 완료 처리한다")
    void completePayoutsMore_Success() {
        // given
        int limit = 10;
        Payout payout1 = mock(Payout.class);
        Payout payout2 = mock(Payout.class);

        given(payoutRepository.findByPayoutDateIsNullAndAmountGreaterThanOrderByIdAsc(any(BigDecimal.class), any(Pageable.class)))
                .willReturn(List.of(payout1, payout2));

        // when
        RsData<Integer> result = payoutCompletePayoutsMoreUseCase.completePayoutsMore(limit);

        // then
        assertThat(result.getData()).isEqualTo(2);
        assertThat(result.getResultCode()).isEqualTo("201-1");

        // 각 Payout의 완료 메서드가 호출되었는지 검증 (가장 중요!)
        verify(payout1, times(1)).completePayout();
        verify(payout2, times(1)).completePayout();
    }

    @Test
    @DisplayName("성공: 처리할 정산건이 없으면 0건 처리 결과를 반환한다")
    void completePayoutsMore_Empty() {
        // given
        given(payoutRepository.findByPayoutDateIsNullAndAmountGreaterThanOrderByIdAsc(any(BigDecimal.class), any(Pageable.class)))
                .willReturn(List.of());

        // when
        RsData<Integer> result = payoutCompletePayoutsMoreUseCase.completePayoutsMore(10);

        // then
        assertThat(result.getData()).isEqualTo(0);
        assertThat(result.getResultCode()).isEqualTo("200-1");
    }

    @Test
    @DisplayName("실패: 배치 제한(limit)이 0 이하면 예외가 발생한다 (에지 케이스)")
    void completePayoutsMore_Fail_InvalidLimit() {
        // when & then
        assertThatThrownBy(() -> payoutCompletePayoutsMoreUseCase.completePayoutsMore(0))
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_BATCH_LIMIT);
    }

    @Test
    @DisplayName("성공: 금액이 0원인 정산건은 조회에서 제외되는지 확인 (쿼리 파라미터 검증)")
    void completePayoutsMore_VerifyQuery() {
        // when
        payoutCompletePayoutsMoreUseCase.completePayoutsMore(10);

        // then
        // 레포지토리를 호출할 때 BigDecimal.ZERO보다 큰 금액을 찾으라고 했는지 확인
        verify(payoutRepository).findByPayoutDateIsNullAndAmountGreaterThanOrderByIdAsc(eq(BigDecimal.ZERO), any(Pageable.class));
    }
}