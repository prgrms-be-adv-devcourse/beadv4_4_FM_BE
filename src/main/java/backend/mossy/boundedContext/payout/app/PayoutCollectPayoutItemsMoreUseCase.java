package backend.mossy.boundedContext.payout.app;

import backend.mossy.boundedContext.payout.domain.*;
import backend.mossy.boundedContext.payout.out.PayoutCandidateItemRepository;
import backend.mossy.boundedContext.payout.out.PayoutRepository;
import backend.mossy.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayoutCollectPayoutItemsMoreUseCase {
    private final PayoutRepository payoutRepository;
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;

    public RsData<Integer> collectPayoutItemsMore(int limit) {
        // 1. 정산할 준비가 된 '정산 후보' 목록을 조회합니다.
        List<PayoutCandidateItem> payoutReadyCandidateItems = findPayoutReadyCandidateItems(limit);

        if (payoutReadyCandidateItems.isEmpty())
            return new RsData<>("200-1", "더 이상 정산에 추가할 항목이 없습니다.", 0);

        payoutReadyCandidateItems.stream()
                .collect(Collectors.groupingBy(PayoutCandidateItem::getPayee))
                .forEach((payee, candidateItems) -> {

                    Payout payout = findActiveByPayee(payee).get();

                    candidateItems.forEach(item -> {
                        PayoutItem payoutItem = payout.addItem(
                                item.getEventType(),
                                item.getRelTypeCode(),
                                item.getRelId(),
                                item.getPaymentDate(),
                                item.getPayer(),
                                item.getPayee(),
                                item.getAmount()
                        );

                        item.setPayoutItem(payoutItem);
                    });
                });


        return new RsData<>(
                "201-1",
                "%d건의 정산데이터가 생성되었습니다.".formatted(payoutReadyCandidateItems.size()),
                payoutReadyCandidateItems.size()
        );
    }

    /**
     * 아직 정산되지 않은(payoutDate가 null인) 특정 판매자(payee)의 Payout 객체를 찾습니다.
     */
    private Optional<Payout> findActiveByPayee(PayoutSeller payee) {
        return payoutRepository.findByPayeeAndPayoutDateIsNull(payee);
    }

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
