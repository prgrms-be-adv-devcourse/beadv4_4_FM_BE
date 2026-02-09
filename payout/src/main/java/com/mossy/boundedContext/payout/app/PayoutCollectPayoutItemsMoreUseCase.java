package com.mossy.boundedContext.payout.app;

import com.mossy.boundedContext.payout.domain.*;
import com.mossy.boundedContext.donation.app.DonationFacade;
import com.mossy.boundedContext.payout.out.PayoutCandidateItemRepository;
import com.mossy.boundedContext.payout.out.PayoutItemRepository;
//import com.mossy.boundedContext.payout.out.PayoutRepository;
import com.mossy.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutCollectPayoutItemsMoreUseCase {
    // 집계 기능 사용 시 필요
    //private final PayoutRepository payoutRepository;

    // 집계 없이 정산만 사용 시 필요
    private final PayoutItemRepository payoutItemRepository;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;
    private final PayoutSupport payoutSupport;
    private final DonationFacade donationFacade;

    // 집계 기능 사용 시 (Payout을 통한 정산)
    // public RsData<Integer> collectPayoutItemsMore(int limit) {
    //     // 1. 정산할 준비가 된 '정산 후보' 목록을 조회합니다.
    //     List<PayoutCandidateItem> payoutReadyCandidateItems = findPayoutReadyCandidateItems(limit);
    //
    //     // 처리할 후보가 없으면 종료합니다.
    //     if (payoutReadyCandidateItems.isEmpty())
    //         return new RsData<>("200-1", "더 이상 정산에 추가할 항목이 없습니다.", 0);
    //
    //     // 2. 후보들을 돈 받을 사람(payee) 기준으로 그룹화합니다.
    //     // 이렇게 하면 판매자별로 여러 판매 건을 한 번에 처리할 수 있습니다.
    //     payoutReadyCandidateItems.stream()
    //             .collect(Collectors.groupingBy(PayoutCandidateItem::getPayee))
    //             .forEach((payee, candidateItems) -> {
    //                 // 3. 해당 판매자의 현재 진행중인(아직 정산되지 않은) Payout 객체를 찾습니다.
    //                 Payout payout = findActiveByPayee(payee)
    //                         .orElseThrow(() -> new DomainException(ErrorCode.PAYOUT_NOT_FOUND));
    //
    //                 // 4. 각 후보(candidateItem)를 실제 정산 항목(payoutItem)으로 변환하여 Payout 객체에 추가합니다.
    //                 candidateItems.forEach(item -> {
    //                     PayoutItem payoutItem = payout.addItem(
    //                             item.getEventType(),
    //                             item.getRelTypeCode(),
    //                             item.getRelId(),
    //                             item.getPaymentDate(),
    //                             item.getPayer(),
    //                             item.getPayee(),
    //                             item.getAmount()
    //                     );
    //
    //                     // 5. 후보 항목에 실제 정산 항목을 연결하여, 이 후보가 처리되었음을 표시합니다.
    //                     item.setPayoutItem(payoutItem);
    //                 });
    //             });
    //
    //
    //     return new RsData<>(
    //             "201-1",
    //             "%d건의 정산데이터가 생성되었습니다.".formatted(payoutReadyCandidateItems.size()),
    //             payoutReadyCandidateItems.size()
    //     );
    // }

    // 집계 없이 정산만 사용 시 (PayoutItem 직접 생성)
    public RsData<Integer> collectPayoutItemsMore(int limit) {
        // 1. 정산할 준비가 된 '정산 후보' 목록을 조회합니다.
        List<PayoutCandidateItem> payoutReadyCandidateItems = findPayoutReadyCandidateItems(limit);

        // 처리할 후보가 없으면 종료합니다.
        if (payoutReadyCandidateItems.isEmpty())
            return new RsData<>("200-1", "더 이상 정산에 추가할 항목이 없습니다.", 0);

        // 기부금 판매자 정보 조회 (기부금 정산 처리용)
        PayoutSeller donationSeller = payoutSupport.findDonationSeller().orElse(null);
        List<Long> donationOrderItemIds = new ArrayList<>();

        // 2. 각 정산 후보를 PayoutItem으로 직접 변환하여 저장합니다.
        payoutReadyCandidateItems.forEach(candidateItem -> {
            PayoutItem payoutItem = PayoutItem.builder()
                    .payout(null)  // 집계 없이 정산만 진행
                    .eventType(candidateItem.getEventType())
                    .relTypeCode(candidateItem.getRelTypeCode())
                    .relId(candidateItem.getRelId())
                    .paymentDate(candidateItem.getPaymentDate())
                    .payer(candidateItem.getPayer())
                    .payee(candidateItem.getPayee())
                    .amount(candidateItem.getAmount())
                    .build();

            // PayoutItem을 직접 저장합니다.
            payoutItemRepository.save(payoutItem);

            // 후보 항목에 실제 정산 항목을 연결하여, 이 후보가 처리되었음을 표시합니다.
            candidateItem.setPayoutItem(payoutItem);

            // 기부금 관련 항목인 경우 orderItemId 수집
            if (donationSeller != null && candidateItem.getPayee().getId().equals(donationSeller.getId())) {
                donationOrderItemIds.add(candidateItem.getRelId());
            }
        });

        // 3. 정산 완료 후 기부금 정산 처리
        if (!donationOrderItemIds.isEmpty()) {
            log.info("정산 완료 후 기부금 정산 처리를 시작합니다. (총 {}건)", donationOrderItemIds.size());
            donationFacade.settleDonationLogsByOrderItemIds(donationOrderItemIds);
            log.info("기부금 정산 처리가 완료되었습니다.");
        }

        return new RsData<>(
                "201-1",
                "%d건의 정산데이터가 생성되었습니다.".formatted(payoutReadyCandidateItems.size()),
                payoutReadyCandidateItems.size()
        );
    }

    /**
     * 아직 정산되지 않은(payoutDate가 null인) 특정 판매자(payee)의 Payout 객체를 찾습니다.
     */
   // private Optional<Payout> findActiveByPayee(PayoutSeller payee) {
   //     return payoutRepository.findByPayeeAndPayoutDateIsNull(payee);
   // }

    /**
     * 정산할 준비가 된 후보들을 조회합니다.
     *
     * @param limit 한 번에 조회할 최대 개수
     * @return 정산 후보 리스트
     */
    private List<PayoutCandidateItem> findPayoutReadyCandidateItems(int limit) {
        // PayoutPolicy에 정의된 대기 기간(예: 7일) 이전의 후보들만 조회합니다.
        // 이것이 바로 환불/교환 등에 대비한 '안전 대기 시간'을 구현하는 핵심 로직입니다.
        LocalDateTime daysAgo = LocalDateTime
                .now()
                .minusDays(PayoutPolicy.PAYOUT_READY_WAITING_DAYS)
                .toLocalDate()
                .atStartOfDay();

        // 아직 PayoutItem으로 변환되지 않았고(payoutItem == null),
        // 결제일이 안전 대기 기간보다 오래된 후보들을 조회합니다.
        return payoutCandidateItemRepository.findByPayoutItemIsNullAndPaymentDateBeforeOrderByPayeeAscIdAsc(
                daysAgo,
                PageRequest.of(0, limit)
        );
    }
}
