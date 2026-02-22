package com.mossy.boundedContext.payout.app.common;

import com.mossy.boundedContext.payout.domain.payout.Payout;
import com.mossy.boundedContext.payout.out.repository.PayoutRepository;
import com.mossy.exception.SuccessCode;
import com.mossy.global.rsData.RsData;
import com.mossy.kafka.KafkaTopics;
import com.mossy.kafka.outbox.service.OutboxPublisher;
import com.mossy.shared.payout.event.PayoutSellerWalletCreditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 정산 완료된 Payout을 판매자 지갑에 지급하는 UseCase
 * 정산 배치와 분리되어 독립적으로 실행됨
 * 판매자별로 합산하여 하루 1개 이벤트만 발행
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutDailyPayoutToWalletUseCase {
    private final PayoutRepository payoutRepository;
    private final OutboxPublisher outboxPublisher;

    /**
     * 정산 완료되었으나 아직 지급되지 않은 Payout들을 조회하여 지급 처리
     * 판매자별로 그룹화하여 합산 금액으로 이벤트 발행
     * Spring Batch의 트랜잭션을 사용하므로 MANDATORY 전파 레벨 사용
     *
     * @param limit 한 번에 처리할 최대 Payout 수
     * @return 처리된 판매자 수
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public RsData<Integer> processDailyPayoutToWallet(int limit) {
        // 1. 정산 완료되었으나 지급되지 않은 Payout 조회 (PESSIMISTIC_WRITE 락)
        // 배치 중복 실행 시 동일 Payout에 대한 중복 지갑 입금 방지
        List<Payout> payoutsToCredit = payoutRepository
                .findCreditTargetsWithLock(PageRequest.of(0, limit));

        if (payoutsToCredit.isEmpty()) {
            log.info("[지급 배치] 지급할 정산이 없습니다.");
            return RsData.success(SuccessCode.PAYOUT_DAILY_WALLET_NOTHING_TO_PROCESS, 0);
        }

        // 2. 각 Payout을 지급 완료 처리 (creditDate 설정)
        payoutsToCredit.forEach(Payout::creditToWallet);

        LocalDate today = LocalDate.now();

        // 3. 판매자별로 그룹화하여 합산
        Map<Long, BigDecimal> amountBySeller = payoutsToCredit.stream()
                .filter(payout -> payout.getPayee() != null)
                .filter(payout -> payout.getPayee().getId() != null)
                .collect(Collectors.groupingBy(
                        payout -> payout.getPayee().getId(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Payout::getAmount,
                                BigDecimal::add
                        )
                ));

        // 4. 판매자별로 지갑 입금 이벤트 발행
        amountBySeller.forEach((sellerId, totalAmount) -> {
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                PayoutSellerWalletCreditEvent event = PayoutSellerWalletCreditEvent.builder()
                        .sellerId(sellerId)
                        .amount(totalAmount)
                        .creditDate(today)
                        .build();

                outboxPublisher.saveEvent(KafkaTopics.PAYOUT_WALLET_CREDIT, "PayoutSeller", sellerId, sellerId.toString(), event);
                log.info("[지급 배치] 판매자 {} - 금액: {}", sellerId, totalAmount);
            }
        });

        log.info("[지급 배치] 완료 - 처리된 판매자: {}명", amountBySeller.size());
        return RsData.success(SuccessCode.PAYOUT_DAILY_WALLET_CREDIT_PROCESSED, amountBySeller.size());
    }
}
