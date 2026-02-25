package com.mossy.boundedContext.payout.app.common;

import com.mossy.boundedContext.payout.domain.payout.Payout;
import com.mossy.boundedContext.payout.domain.seller.PayoutSeller;
import com.mossy.boundedContext.payout.in.dto.response.PayoutListResponseDto;
import com.mossy.boundedContext.payout.in.dto.response.PayoutResponseDto;
import com.mossy.boundedContext.payout.out.repository.PayoutRepository;
import com.mossy.boundedContext.payout.out.repository.PayoutSellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayoutQueryUseCase {

    private final PayoutSellerRepository payoutSellerRepository;
    private final PayoutRepository payoutRepository;

    public PayoutListResponseDto findMonthlyPayouts(Long userId, int year, int month) {
        Optional<PayoutSeller> sellerOpt = payoutSellerRepository.findByUserId(userId);

        if (sellerOpt.isEmpty()) {
            return emptyResult();
        }

        PayoutSeller seller = sellerOpt.get();
        LocalDateTime from = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime to = from.plusMonths(1);

        List<Payout> payouts = payoutRepository
                .findByPayeeAndPayoutDateBetweenOrderByPayoutDateDesc(seller, from, to);

        BigDecimal totalAmount = payouts.stream()
                .map(Payout::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal creditedAmount = payouts.stream()
                .filter(Payout::isCredited)
                .map(Payout::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingCreditAmount = totalAmount.subtract(creditedAmount);

        PayoutListResponseDto.Summary summary = new PayoutListResponseDto.Summary(
                totalAmount, creditedAmount, pendingCreditAmount
        );

        List<PayoutResponseDto> payoutDtos = payouts.stream()
                .map(PayoutResponseDto::from)
                .toList();

        return new PayoutListResponseDto(summary, payoutDtos);
    }

    private PayoutListResponseDto emptyResult() {
        PayoutListResponseDto.Summary summary = new PayoutListResponseDto.Summary(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );
        return new PayoutListResponseDto(summary, Collections.emptyList());
    }
}
