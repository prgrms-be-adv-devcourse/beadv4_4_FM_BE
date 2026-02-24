package com.mossy.boundedContext.donation.app.common;

import com.mossy.boundedContext.donation.in.dto.response.DonationLogResponseDto;
import com.mossy.boundedContext.donation.out.DonationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
}
