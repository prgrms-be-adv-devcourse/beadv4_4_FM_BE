package backend.mossy.boundedContext.payout.app;

import backend.mossy.boundedContext.payout.domain.Payout;
import backend.mossy.boundedContext.payout.out.PayoutRepository;
import backend.mossy.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayoutCompletePayoutsMoreUseCase {
    private final PayoutRepository payoutRepository;

    public RsData<Integer> completePayoutsMore(int limit) {
        List<Payout> activePayouts = findActivePayouts(limit);

        if (activePayouts.isEmpty())
            return new RsData<>("200-1", "더 이상 정산할 정산내역이 없습니다.", 0);

        activePayouts.forEach(Payout::completePayout);

        return new RsData<>(
                "201-1",
                "%d건의 정산이 처리되었습니다.".formatted(activePayouts.size()),
                activePayouts.size()
        );
    }

    private List<Payout> findActivePayouts(BigDecimal limit) {
        return payoutRepository.findByPayoutDateIsNullAndAmountGreaterThanOrderByIdAsc(0, PageRequest.of(0, limit));
    }
}