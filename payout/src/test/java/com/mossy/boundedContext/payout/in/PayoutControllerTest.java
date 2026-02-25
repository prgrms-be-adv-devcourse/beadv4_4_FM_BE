package com.mossy.boundedContext.payout.in;

import com.mossy.boundedContext.payout.app.common.PayoutQueryUseCase;
import com.mossy.boundedContext.payout.in.dto.response.PayoutListResponseDto;
import com.mossy.boundedContext.payout.in.dto.response.PayoutResponseDto;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PayoutControllerTest {

    @Mock
    private PayoutQueryUseCase payoutQueryUseCase;

    @InjectMocks
    private PayoutController payoutController;

    // ===== 파라미터 전달 검증 =====

    @Test
    @DisplayName("year/month 명시 시 UseCase에 해당 값 그대로 전달")
    void getMonthlyPayouts_explicit_year_month_passed_to_useCase() {
        Long userId = 1L;
        int year = 2026;
        int month = 2;
        when(payoutQueryUseCase.findMonthlyPayouts(userId, year, month))
                .thenReturn(emptyResult());

        payoutController.getMonthlyPayouts(userId, year, month);

        verify(payoutQueryUseCase).findMonthlyPayouts(userId, year, month);
    }

    @Test
    @DisplayName("year/month null이면 현재 연월을 UseCase에 전달")
    void getMonthlyPayouts_null_year_month_uses_current_date() {
        Long userId = 1L;
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        when(payoutQueryUseCase.findMonthlyPayouts(userId, currentYear, currentMonth))
                .thenReturn(emptyResult());

        payoutController.getMonthlyPayouts(userId, null, null);

        verify(payoutQueryUseCase).findMonthlyPayouts(userId, currentYear, currentMonth);
    }

    @Test
    @DisplayName("year만 입력하고 month null이면 현재 월 사용")
    void getMonthlyPayouts_year_only_uses_current_month() {
        Long userId = 1L;
        int year = 2025;
        int currentMonth = LocalDate.now().getMonthValue();

        ArgumentCaptor<Integer> monthCaptor = ArgumentCaptor.forClass(Integer.class);
        when(payoutQueryUseCase.findMonthlyPayouts(eq(userId), eq(year), monthCaptor.capture()))
                .thenReturn(emptyResult());

        payoutController.getMonthlyPayouts(userId, year, null);

        assertThat(monthCaptor.getValue()).isEqualTo(currentMonth);
    }

    // ===== 응답 포맷 검증 =====

    @Test
    @DisplayName("응답 resultCode는 S-200")
    void getMonthlyPayouts_response_result_code_is_S_200() {
        Long userId = 1L;
        when(payoutQueryUseCase.findMonthlyPayouts(eq(userId), anyInt(), anyInt()))
                .thenReturn(emptyResult());

        RsData<PayoutListResponseDto> response = payoutController.getMonthlyPayouts(userId, 2026, 2);

        assertThat(response.getResultCode()).isEqualTo("S-200");
    }

    @Test
    @DisplayName("응답 msg는 정산 목록 조회에 성공했습니다.")
    void getMonthlyPayouts_response_msg() {
        Long userId = 1L;
        when(payoutQueryUseCase.findMonthlyPayouts(eq(userId), anyInt(), anyInt()))
                .thenReturn(emptyResult());

        RsData<PayoutListResponseDto> response = payoutController.getMonthlyPayouts(userId, 2026, 2);

        assertThat(response.getMsg()).isEqualTo(SuccessCode.PAYOUT_LIST_FOUND.getMsg());
    }

    @Test
    @DisplayName("UseCase 반환값이 응답 data에 담김")
    void getMonthlyPayouts_response_data_equals_useCase_result() {
        Long userId = 1L;
        PayoutListResponseDto expected = resultWithAmount(new BigDecimal("500000"));
        when(payoutQueryUseCase.findMonthlyPayouts(userId, 2026, 2)).thenReturn(expected);

        RsData<PayoutListResponseDto> response = payoutController.getMonthlyPayouts(userId, 2026, 2);

        assertThat(response.getData()).isEqualTo(expected);
    }

    // ===== 비즈니스 시나리오 =====

    @Test
    @DisplayName("판매자가 없으면 summary 금액이 모두 0이고 목록이 비어있음")
    void getMonthlyPayouts_unknown_seller_returns_empty_result() {
        Long userId = 999L;
        when(payoutQueryUseCase.findMonthlyPayouts(eq(userId), anyInt(), anyInt()))
                .thenReturn(emptyResult());

        RsData<PayoutListResponseDto> response = payoutController.getMonthlyPayouts(userId, 2026, 2);
        PayoutListResponseDto data = response.getData();

        assertThat(data.summary().totalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(data.summary().creditedAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(data.summary().pendingCreditAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(data.payouts()).isEmpty();
    }

    @Test
    @DisplayName("정산 완료 + 지급 완료 항목이 있으면 summary에 반영됨")
    void getMonthlyPayouts_with_credited_payout_reflects_in_summary() {
        Long userId = 1L;
        BigDecimal totalAmount = new BigDecimal("500000");
        BigDecimal creditedAmount = new BigDecimal("380000");
        BigDecimal pendingAmount = new BigDecimal("120000");

        PayoutListResponseDto.Summary summary = new PayoutListResponseDto.Summary(
                totalAmount, creditedAmount, pendingAmount
        );
        PayoutResponseDto payoutDto = PayoutResponseDto.builder()
                .id(1L)
                .status("CREDITED")
                .amount(creditedAmount)
                .payoutDate(LocalDateTime.of(2026, 2, 10, 0, 0))
                .creditDate(LocalDateTime.of(2026, 2, 25, 0, 0))
                .build();

        PayoutListResponseDto result = new PayoutListResponseDto(summary, List.of(payoutDto));
        when(payoutQueryUseCase.findMonthlyPayouts(userId, 2026, 2)).thenReturn(result);

        RsData<PayoutListResponseDto> response = payoutController.getMonthlyPayouts(userId, 2026, 2);
        PayoutListResponseDto data = response.getData();

        assertThat(data.summary().totalAmount()).isEqualByComparingTo(totalAmount);
        assertThat(data.summary().creditedAmount()).isEqualByComparingTo(creditedAmount);
        assertThat(data.summary().pendingCreditAmount()).isEqualByComparingTo(pendingAmount);
        assertThat(data.payouts()).hasSize(1);
        assertThat(data.payouts().get(0).getStatus()).isEqualTo("CREDITED");
    }

    @Test
    @DisplayName("정산 완료되었지만 지급 미완료 항목은 status가 COMPLETED")
    void getMonthlyPayouts_completed_but_not_credited_status_is_COMPLETED() {
        Long userId = 1L;
        PayoutResponseDto payoutDto = PayoutResponseDto.builder()
                .id(2L)
                .status("COMPLETED")
                .amount(new BigDecimal("200000"))
                .payoutDate(LocalDateTime.of(2026, 2, 20, 0, 0))
                .creditDate(null)
                .build();

        PayoutListResponseDto result = new PayoutListResponseDto(
                new PayoutListResponseDto.Summary(
                        new BigDecimal("200000"), BigDecimal.ZERO, new BigDecimal("200000")
                ),
                List.of(payoutDto)
        );
        when(payoutQueryUseCase.findMonthlyPayouts(userId, 2026, 2)).thenReturn(result);

        RsData<PayoutListResponseDto> response = payoutController.getMonthlyPayouts(userId, 2026, 2);
        PayoutResponseDto dto = response.getData().payouts().get(0);

        assertThat(dto.getStatus()).isEqualTo("COMPLETED");
        assertThat(dto.getCreditDate()).isNull();
    }

    // ===== 헬퍼 메서드 =====

    private PayoutListResponseDto emptyResult() {
        return new PayoutListResponseDto(
                new PayoutListResponseDto.Summary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                Collections.emptyList()
        );
    }

    private PayoutListResponseDto resultWithAmount(BigDecimal totalAmount) {
        return new PayoutListResponseDto(
                new PayoutListResponseDto.Summary(totalAmount, BigDecimal.ZERO, totalAmount),
                Collections.emptyList()
        );
    }
}
