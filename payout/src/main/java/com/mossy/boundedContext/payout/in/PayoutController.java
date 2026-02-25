package com.mossy.boundedContext.payout.in;

import com.mossy.boundedContext.payout.app.common.PayoutQueryUseCase;
import com.mossy.boundedContext.payout.in.dto.response.PayoutListResponseDto;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Payout", description = "판매자 정산 조회 API")
@RestController
@RequestMapping("/api/v1/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutQueryUseCase payoutQueryUseCase;

    @Operation(summary = "월별 정산 조회", description = "해당 월의 정산 요약 및 목록을 조회합니다. year/month 미입력 시 현재 월로 조회합니다.")
    @GetMapping
    public RsData<PayoutListResponseDto> getMonthlyPayouts(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        int targetMonth = (month != null) ? month : LocalDate.now().getMonthValue();

        PayoutListResponseDto result = payoutQueryUseCase.findMonthlyPayouts(userId, targetYear, targetMonth);
        return RsData.success(SuccessCode.PAYOUT_LIST_FOUND, result);
    }
}
