package com.mossy.boundedContext.payout.app.common;

import com.mossy.boundedContext.payout.domain.payout.PayoutCandidateItem;
import com.mossy.boundedContext.payout.domain.payout.PayoutItem;
import com.mossy.boundedContext.payout.out.repository.PayoutCandidateItemRepository;
import com.mossy.exception.DomainException;
import com.mossy.exception.ErrorCode;
import com.mossy.shared.cash.enums.SellerEventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayoutRefundUseCaseTest {

    @Mock
    private PayoutCandidateItemRepository candidateItemRepository;

    @Mock
    private PayoutMapper payoutMapper;

    @InjectMocks
    private PayoutRefundUseCase payoutRefundUseCase;

    // ===== 예외 케이스 =====

    @Test
    @DisplayName("orderItemId에 해당하는 정산 후보가 없으면 PAYOUT_CANDIDATE_NOT_FOUND")
    void processRefund_empty_candidates_throws_exception() {
        when(candidateItemRepository.findByRelIdAndRelTypeCodeWithLock(1L, "OrderItem"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> payoutRefundUseCase.processRefund(1L, new BigDecimal("1000"), new BigDecimal("1000")))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(ErrorCode.PAYOUT_CANDIDATE_NOT_FOUND.getMsg());
    }

    @Test
    @DisplayName("정산이 이미 완료된 후보가 있으면 REFUND_NOT_ALLOWED_AFTER_PAYOUT_COMPLETED")
    void processRefund_already_settled_candidate_throws_exception() {
        PayoutCandidateItem settledCandidate = mockCandidate(
                SellerEventType.정산__상품판매_대금, new BigDecimal("1000"), BigDecimal.ZERO, mock(PayoutItem.class)
        );

        when(candidateItemRepository.findByRelIdAndRelTypeCodeWithLock(1L, "OrderItem"))
                .thenReturn(List.of(settledCandidate));

        assertThatThrownBy(() -> payoutRefundUseCase.processRefund(1L, new BigDecimal("1000"), new BigDecimal("1000")))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(ErrorCode.REFUND_NOT_ALLOWED_AFTER_PAYOUT_COMPLETED.getMsg());
    }

    // ===== 단건 전액 환불 =====

    @Test
    @DisplayName("단건 전액 환불: save 1회 호출, 음수 금액으로 createRefundItem 호출")
    void processRefund_single_candidate_full_refund() {
        PayoutCandidateItem candidate = mockCandidate(
                SellerEventType.정산__상품판매_대금, new BigDecimal("1000"), new BigDecimal("0.5"), null
        );

        when(candidateItemRepository.findByRelIdAndRelTypeCodeWithLock(1L, "OrderItem"))
                .thenReturn(List.of(candidate));
        when(payoutMapper.createRefundItem(any(), any(), any(), any()))
                .thenReturn(mock(PayoutCandidateItem.class));

        payoutRefundUseCase.processRefund(1L, new BigDecimal("1000"), new BigDecimal("1000"));

        // 환불 아이템 생성 시 refundAmount가 음수(-1000)로 전달되어야 함
        verify(payoutMapper).createRefundItem(
                eq(candidate),
                eq(SellerEventType.정산__상품환불_대금),
                eq(new BigDecimal("-1000")),
                any()
        );
        verify(candidateItemRepository, times(1)).save(any());
    }

    // ===== 비율 배분 =====

    @Test
    @DisplayName("2개 후보(600:400) 환불 1000 → 각각 -600, -400으로 생성")
    void processRefund_two_candidates_proportional_allocation() {
        PayoutCandidateItem candidateA = mockCandidate(
                SellerEventType.정산__상품판매_대금, new BigDecimal("600"), BigDecimal.ZERO, null
        );
        PayoutCandidateItem candidateB = mockCandidate(
                SellerEventType.정산__상품판매_수수료, new BigDecimal("400"), BigDecimal.ZERO, null
        );

        when(candidateItemRepository.findByRelIdAndRelTypeCodeWithLock(1L, "OrderItem"))
                .thenReturn(List.of(candidateA, candidateB));
        when(payoutMapper.createRefundItem(any(), any(), any(), any()))
                .thenReturn(mock(PayoutCandidateItem.class));

        payoutRefundUseCase.processRefund(1L, new BigDecimal("1000"), new BigDecimal("1000"));

        // A: 600/1000 × 1000 = 600 → -600
        verify(payoutMapper).createRefundItem(
                eq(candidateA), eq(SellerEventType.정산__상품환불_대금),
                eq(new BigDecimal("-600")), any()
        );
        // B: 400/1000 × 1000 = 400 → -400
        verify(payoutMapper).createRefundItem(
                eq(candidateB), eq(SellerEventType.정산__상품환불_수수료),
                eq(new BigDecimal("-400")), any()
        );
    }

    // ===== 잔여금 보정 =====

    @Test
    @DisplayName("균등 3분할 불가 시 잔여금을 마지막 항목에 보정 (합계 = refundAmount)")
    void processRefund_remainder_added_to_last_candidate() {
        // amount 1:1:1 → total=3, refundAmount=10
        // 각 item → 10 × (1/3) = 3.33... → floor → 3
        // allocatedSum = 9, remainder = 1 → 마지막에 +1 → 4
        PayoutCandidateItem cA = mockCandidate(SellerEventType.정산__상품판매_대금, new BigDecimal("1"), BigDecimal.ZERO, null);
        PayoutCandidateItem cB = mockCandidate(SellerEventType.정산__상품판매_수수료, new BigDecimal("1"), BigDecimal.ZERO, null);
        PayoutCandidateItem cC = mockCandidate(SellerEventType.정산__상품판매_기부금, new BigDecimal("1"), BigDecimal.ZERO, null);

        when(candidateItemRepository.findByRelIdAndRelTypeCodeWithLock(1L, "OrderItem"))
                .thenReturn(List.of(cA, cB, cC));
        when(payoutMapper.createRefundItem(any(), any(), any(), any()))
                .thenReturn(mock(PayoutCandidateItem.class));

        payoutRefundUseCase.processRefund(1L, new BigDecimal("10"), new BigDecimal("10"));

        // ArgumentCaptor로 전달된 환불 금액 캡처
        ArgumentCaptor<BigDecimal> amountCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(payoutMapper, times(3)).createRefundItem(any(), any(), amountCaptor.capture(), any());

        List<BigDecimal> capturedAmounts = amountCaptor.getAllValues();

        // 세 항목의 환불 금액 합계가 -10이어야 함 (음수)
        BigDecimal total = capturedAmounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(total).isEqualByComparingTo(new BigDecimal("-10"));

        // 마지막 항목이 보정을 받아야 함 (-3, -3이 아닌 -4)
        assertThat(capturedAmounts.get(2)).isEqualByComparingTo(new BigDecimal("-4"));
    }

    // ===== 환불 타입 변환 =====

    @Test
    @DisplayName("정산__상품판매_대금 → 정산__상품환불_대금 타입으로 변환")
    void convertToRefundEventType_판매대금_to_환불대금() {
        PayoutCandidateItem candidate = mockCandidate(
                SellerEventType.정산__상품판매_대금, new BigDecimal("1000"), BigDecimal.ZERO, null
        );
        when(candidateItemRepository.findByRelIdAndRelTypeCodeWithLock(any(), any()))
                .thenReturn(List.of(candidate));
        when(payoutMapper.createRefundItem(any(), any(), any(), any()))
                .thenReturn(mock(PayoutCandidateItem.class));

        payoutRefundUseCase.processRefund(1L, new BigDecimal("1000"), new BigDecimal("1000"));

        verify(payoutMapper).createRefundItem(any(), eq(SellerEventType.정산__상품환불_대금), any(), any());
    }

    @Test
    @DisplayName("정산__상품판매_수수료 → 정산__상품환불_수수료 타입으로 변환")
    void convertToRefundEventType_판매수수료_to_환불수수료() {
        PayoutCandidateItem candidate = mockCandidate(
                SellerEventType.정산__상품판매_수수료, new BigDecimal("200"), BigDecimal.ZERO, null
        );
        when(candidateItemRepository.findByRelIdAndRelTypeCodeWithLock(any(), any()))
                .thenReturn(List.of(candidate));
        when(payoutMapper.createRefundItem(any(), any(), any(), any()))
                .thenReturn(mock(PayoutCandidateItem.class));

        payoutRefundUseCase.processRefund(1L, new BigDecimal("200"), new BigDecimal("200"));

        verify(payoutMapper).createRefundItem(any(), eq(SellerEventType.정산__상품환불_수수료), any(), any());
    }

    @Test
    @DisplayName("정산__상품판매_기부금 → 정산__상품환불_기부금 타입으로 변환")
    void convertToRefundEventType_판매기부금_to_환불기부금() {
        PayoutCandidateItem candidate = mockCandidate(
                SellerEventType.정산__상품판매_기부금, new BigDecimal("50"), BigDecimal.ZERO, null
        );
        when(candidateItemRepository.findByRelIdAndRelTypeCodeWithLock(any(), any()))
                .thenReturn(List.of(candidate));
        when(payoutMapper.createRefundItem(any(), any(), any(), any()))
                .thenReturn(mock(PayoutCandidateItem.class));

        payoutRefundUseCase.processRefund(1L, new BigDecimal("50"), new BigDecimal("50"));

        verify(payoutMapper).createRefundItem(any(), eq(SellerEventType.정산__상품환불_기부금), any(), any());
    }

    // ===== 플랫폼 쿠폰 분리 처리 =====

    @Test
    @DisplayName("플랫폼 쿠폰 항목은 buyerPaidAmount 비율로 별도 계산")
    void processRefund_platform_coupon_refunded_by_ratio() {
        // buyer: 대금=1000 / platform: 프로모션=200
        // refundAmount=500, buyerPaidAmount=1000
        // platform ratio = 500/1000 = 0.5 → platform refund = 200 × 0.5 = 100
        PayoutCandidateItem buyerItem = mockCandidate(
                SellerEventType.정산__상품판매_대금, new BigDecimal("1000"), BigDecimal.ZERO, null
        );
        PayoutCandidateItem platformItem = mockCandidate(
                SellerEventType.정산__프로모션_플랫폼부담, new BigDecimal("200"), BigDecimal.ZERO, null
        );

        when(candidateItemRepository.findByRelIdAndRelTypeCodeWithLock(1L, "OrderItem"))
                .thenReturn(List.of(buyerItem, platformItem));
        when(payoutMapper.createRefundItem(any(), any(), any(), any()))
                .thenReturn(mock(PayoutCandidateItem.class));

        payoutRefundUseCase.processRefund(1L, new BigDecimal("500"), new BigDecimal("1000"));

        // buyer 항목: -500
        verify(payoutMapper).createRefundItem(
                eq(buyerItem), eq(SellerEventType.정산__상품환불_대금),
                eq(new BigDecimal("-500")), any()
        );
        // platform 항목: -(200 × 0.5) = -100
        verify(payoutMapper).createRefundItem(
                eq(platformItem), eq(SellerEventType.정산__상품환불_플랫폼부담),
                eq(new BigDecimal("-100")), any()
        );

        // save는 총 2회 (buyer 1 + platform 1)
        verify(candidateItemRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("플랫폼 쿠폰 항목이 없으면 buyer만 처리하고 save 1회")
    void processRefund_no_platform_coupon_only_buyer_processed() {
        PayoutCandidateItem buyerItem = mockCandidate(
                SellerEventType.정산__상품판매_대금, new BigDecimal("1000"), BigDecimal.ZERO, null
        );

        when(candidateItemRepository.findByRelIdAndRelTypeCodeWithLock(1L, "OrderItem"))
                .thenReturn(List.of(buyerItem));
        when(payoutMapper.createRefundItem(any(), any(), any(), any()))
                .thenReturn(mock(PayoutCandidateItem.class));

        payoutRefundUseCase.processRefund(1L, new BigDecimal("1000"), new BigDecimal("1000"));

        verify(candidateItemRepository, times(1)).save(any());
    }

    // ===== 탄소 음수 변환 =====

    @Test
    @DisplayName("환불 탄소량도 비율로 계산 후 음수 전달")
    void processRefund_carbon_negated_by_ratio() {
        // amount=1000, carbonKg=1.0, 전액 환불
        // ratio = 1.0 → itemRefundCarbon = 1.0 × 1.0 = 1.00 → negate → -1.00
        PayoutCandidateItem candidate = mockCandidate(
                SellerEventType.정산__상품판매_대금, new BigDecimal("1000"), new BigDecimal("1.00"), null
        );
        when(candidateItemRepository.findByRelIdAndRelTypeCodeWithLock(any(), any()))
                .thenReturn(List.of(candidate));
        when(payoutMapper.createRefundItem(any(), any(), any(), any()))
                .thenReturn(mock(PayoutCandidateItem.class));

        payoutRefundUseCase.processRefund(1L, new BigDecimal("1000"), new BigDecimal("1000"));

        ArgumentCaptor<BigDecimal> carbonCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(payoutMapper).createRefundItem(any(), any(), any(), carbonCaptor.capture());

        assertThat(carbonCaptor.getValue()).isEqualByComparingTo(new BigDecimal("-1.00"));
    }

    // ===== 헬퍼 메서드 =====

    /**
     * 헬퍼 메서드에서 생성하는 stub은 테스트 케이스에 따라 일부만 호출될 수 있으므로
     * lenient()로 등록하여 UnnecessaryStubbingException을 방지
     */
    private PayoutCandidateItem mockCandidate(
            SellerEventType eventType,
            BigDecimal amount,
            BigDecimal carbonKg,
            PayoutItem payoutItem
    ) {
        PayoutCandidateItem candidate = mock(PayoutCandidateItem.class);
        lenient().when(candidate.getEventType()).thenReturn(eventType);
        lenient().when(candidate.getAmount()).thenReturn(amount);
        lenient().when(candidate.getCarbonKg()).thenReturn(carbonKg);
        lenient().when(candidate.getPayoutItem()).thenReturn(payoutItem);
        return candidate;
    }
}
