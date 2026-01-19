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
    private final PayoutCandidateItemRepository payoutCandidateItemRepository;
    private final PayoutRepository payoutRepository;

    public RsData<Integer> collectPayoutItemsMore(int limit) {
        // 1. 정산할 준비가 된 '정산 후보' 목록을 조회합니다.
        List<PayoutCandidateItem> payoutReadyCandidateItems = findPayoutReadyCandidateItems(limit);

        // 처리할 후보가 없으면 종료합니다.
        if (payoutReadyCandidateItems.isEmpty())
            return new RsData<>("200-1", "더 이상 정산에 추가할 항목이 없습니다.", 0);

        // 2. 후보들을 돈 받을 사람(payee) 기준으로 그룹화합니다.
        // 이렇게 하면 판매자별로 여러 판매 건을 한 번에 처리할 수 있습니다.
        payoutReadyCandidateItems.stream()
                .collect(Collectors.groupingBy(PayoutCandidateItem::getPayee))
                .forEach((payee, candidateItems) -> {
                    // 3. 해당 판매자의 현재 진행중인(아직 정산되지 않은) Payout 객체를 찾습니다.
                    Payout payout = findActiveByPayee(payee).get();

                    // 4. 각 후보(candidateItem)를 실제 정산 항목(payoutItem)으로 변환하여 Payout 객체에 추가합니다.
                    // PayoutCandidateItem과 PayoutItem의 양방향 연결은 Payout.addItem()에서 자동으로 처리됩니다.
                    candidateItems.forEach(item -> {
                        payout.addItem(
                                item.getEventType(),
                                item.getRelTypeCode(),
                                item.getRelId(),
                                item.getPaymentDate(),
                                item.getPayee(),
                                item.getAmount(),
                                item  // PayoutCandidateItem 전달 - 양방향 연결이 자동으로 처리됨
                        );
                    });
                });


        return new RsData<>(
                "201-1",
                "%d건의 정산데이터가 생성되었습니다.".formatted(payoutReadyCandidateItems.size()),
                payoutReadyCandidateItems.size()
        );
    }

    private Optional<Payout> findActiveByPayee(PayoutSeller payee) {
        return payoutRepository.findByPayeeAndPayoutDateIsNull(payee);
    }

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