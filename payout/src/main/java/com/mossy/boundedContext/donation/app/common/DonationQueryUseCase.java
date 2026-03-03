package com.mossy.boundedContext.donation.app.common;

import com.mossy.boundedContext.donation.domain.DonationLog;
import com.mossy.boundedContext.donation.in.dto.response.DonationLogResponseDto;
import com.mossy.boundedContext.donation.in.dto.response.DonationMonthlyHistoryResponseDto;
import com.mossy.boundedContext.donation.in.dto.response.DonationSummaryResponseDto;
import com.mossy.boundedContext.donation.out.DonationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonationQueryUseCase {

    private final DonationLogRepository donationLogRepository;

    @Transactional(readOnly = true)
    public List<DonationLogResponseDto> findMonthlyDonationLogs(Long userId, int year, int month) {
        LocalDateTime from = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime to = from.plusMonths(1);

        return donationLogRepository
                .findByUser_IdAndCreatedAtBetween(userId, from, to)
                .stream()
                .map(DonationLogResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DonationSummaryResponseDto getDonationSummary(Long userId) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        LocalDateTime from = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime to = from.plusMonths(1);

        List<DonationLog> logs = donationLogRepository.findByUser_IdAndCreatedAtBetween(userId, from, to);
        return DonationSummaryResponseDto.from(year, month, logs);
    }

    @Transactional(readOnly = true)
    public List<DonationMonthlyHistoryResponseDto> getMonthlyDonationHistory(Long userId) {
        List<DonationLog> all = donationLogRepository.findByUser_IdOrderByCreatedAtDesc(userId);

        Map<YearMonth, List<DonationLog>> grouped = all.stream()
                .collect(Collectors.groupingBy(
                        log -> YearMonth.from(log.getCreatedAt()),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return grouped.entrySet().stream()
                .map(entry -> {
                    YearMonth ym = entry.getKey();
                    List<DonationLog> monthLogs = entry.getValue();
                    BigDecimal totalAmount = monthLogs.stream()
                            .map(DonationLog::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalCarbonOffset = monthLogs.stream()
                            .map(DonationLog::getCarbonOffset)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    List<DonationLogResponseDto> dtos = monthLogs.stream()
                            .map(DonationLogResponseDto::from)
                            .toList();
                    return DonationMonthlyHistoryResponseDto.builder()
                            .year(ym.getYear())
                            .month(ym.getMonthValue())
                            .totalAmount(totalAmount)
                            .totalCarbonOffset(totalCarbonOffset)
                            .donationCount(monthLogs.size())
                            .logs(dtos)
                            .build();
                })
                .toList();
    }
}
