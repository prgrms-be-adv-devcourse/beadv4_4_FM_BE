package com.mossy.boundedContext.donation.in;

import com.mossy.boundedContext.donation.app.common.DonationQueryUseCase;
import com.mossy.boundedContext.donation.in.dto.response.DonationLogResponseDto;
import com.mossy.boundedContext.donation.in.dto.response.DonationMonthlyHistoryResponseDto;
import com.mossy.boundedContext.donation.in.dto.response.DonationSummaryResponseDto;
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
import java.util.List;

@Tag(name = "Donation", description = "기부 내역 조회 API")
@RestController
@RequestMapping("/api/v1/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationQueryUseCase donationQueryUseCase;

    @Operation(summary = "이번 달 기부 요약 조회", description = "구매자의 이번 달 기부 총액, 탄소 절감량, 건수를 조회합니다.")
    @GetMapping("/summary")
    public RsData<DonationSummaryResponseDto> getDonationSummary(
            @RequestHeader("X-User-Id") Long userId) {

        DonationSummaryResponseDto summary = donationQueryUseCase.getDonationSummary(userId);
        return RsData.success(SuccessCode.DONATION_SUMMARY_FOUND, summary);
    }

    @Operation(summary = "달별 기부 내역 전체 조회", description = "구매자의 전체 기부 내역을 달별로 묶어서 최신순으로 조회합니다.")
    @GetMapping("/history")
    public RsData<List<DonationMonthlyHistoryResponseDto>> getMonthlyDonationHistory(
            @RequestHeader("X-User-Id") Long userId) {

        List<DonationMonthlyHistoryResponseDto> history = donationQueryUseCase.getMonthlyDonationHistory(userId);
        return RsData.success(SuccessCode.DONATION_HISTORY_FOUND, history);
    }

    @Operation(summary = "월별 기부 내역 조회", description = "해당 월의 기부 내역을 조회합니다. year/month 미입력 시 현재 월로 조회합니다.")
    @GetMapping("/monthly")
    public RsData<List<DonationLogResponseDto>> getMonthlyDonationLogs(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        int targetMonth = (month != null) ? month : LocalDate.now().getMonthValue();

        List<DonationLogResponseDto> logs = donationQueryUseCase.findMonthlyDonationLogs(userId, targetYear, targetMonth);
        return RsData.success(SuccessCode.DONATION_LOGS_FOUND, logs);
    }
}
