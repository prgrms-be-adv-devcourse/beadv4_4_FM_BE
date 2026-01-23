package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.*;
import backend.mossy.boundedContext.payout.out.payout.PayoutCandidateItemRepository;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.global.exception.DomainException;
import backend.mossy.global.rsData.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayoutCollectPayoutItemsMoreUseCaseTest {

    @InjectMocks
    private PayoutCollectPayoutItemsMoreUseCase payoutCollectPayoutItemsMoreUseCase;

    @Mock
    private PayoutRepository payoutRepository;

    @Mock
    private PayoutCandidateItemRepository payoutCandidateItemRepository;

    @Test
    @DisplayName("성공: 대기 기간이 지난 정산 후보들을 기존 Payout에 추가한다")
    void collectPayoutItemsMore_Success() {
        // given
        int limit = 10;
        PayoutSeller payee = mock(PayoutSeller.class);

        // 정산 대기 기간이 지난 후보 아이템 생성 (Mock)
        PayoutCandidateItem candidateItem = mock(PayoutCandidateItem.class);
        given(candidateItem.getPayee()).willReturn(payee);

        given(payoutCandidateItemRepository.findByPayoutItemIsNullAndPaymentDateBeforeOrderByPayeeAscIdAsc(any(LocalDateTime.class), any(Pageable.class)))
                .willReturn(List.of(candidateItem));

        // 해당 판매자의 활성화된 Payout 모킹
        Payout activePayout = mock(Payout.class);
        given(payoutRepository.findByPayeeAndPayoutDateIsNull(payee)).willReturn(Optional.of(activePayout));

        // when
        RsData<Integer> result = payoutCollectPayoutItemsMoreUseCase.collectPayoutItemsMore(limit);

        // then
        assertThat(result.getData()).isEqualTo(1); // 1건 처리 확인
        verify(activePayout).addItem(any(), any(), any(), any(), any(), any(), any()); // Payout에 아이템 추가 호출 확인
        verify(candidateItem).setPayoutItem(any()); // 연결 로직 확인
    }

    @Test
    @DisplayName("실패: 진행 중인 Payout(정산서)이 없으면 예외가 발생한다")
    void collectPayoutItemsMore_Fail_PayoutNotFound() {
        // given
        PayoutSeller payee = mock(PayoutSeller.class);
        PayoutCandidateItem candidateItem = mock(PayoutCandidateItem.class);
        given(candidateItem.getPayee()).willReturn(payee);

        given(payoutCandidateItemRepository.findByPayoutItemIsNullAndPaymentDateBeforeOrderByPayeeAscIdAsc(any(LocalDateTime.class), any(Pageable.class)))
                .willReturn(List.of(candidateItem));

        // Payout이 존재하지 않는 상황 가정
        given(payoutRepository.findByPayeeAndPayoutDateIsNull(payee)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> payoutCollectPayoutItemsMoreUseCase.collectPayoutItemsMore(10))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("성공: 처리할 후보가 없으면 0건 처리 결과를 반환한다")
    void collectPayoutItemsMore_Empty() {
        // given
        given(payoutCandidateItemRepository.findByPayoutItemIsNullAndPaymentDateBeforeOrderByPayeeAscIdAsc(any(LocalDateTime.class), any(Pageable.class)))
                .willReturn(List.of());

        // when
        RsData<Integer> result = payoutCollectPayoutItemsMoreUseCase.collectPayoutItemsMore(10);

        // then
        assertThat(result.getData()).isEqualTo(0);
        assertThat(result.getResultCode()).isEqualTo("200-1");
    }
}