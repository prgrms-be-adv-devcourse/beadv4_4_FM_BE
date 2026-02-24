package com.mossy.boundedContext.donation.in;

import com.mossy.boundedContext.donation.app.common.DonationQueryUseCase;
import com.mossy.boundedContext.donation.in.dto.response.DonationLogResponseDto;
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
