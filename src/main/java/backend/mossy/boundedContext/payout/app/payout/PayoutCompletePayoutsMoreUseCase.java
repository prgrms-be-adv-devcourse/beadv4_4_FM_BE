package backend.mossy.boundedContext.payout.app.payout;

import backend.mossy.boundedContext.payout.domain.payout.Payout;
import backend.mossy.boundedContext.payout.out.payout.PayoutRepository;
import backend.mossy.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    private List<Payout> findActivePayouts(int limit) {
        return payoutRepository.findByPayoutDateIsNullAndAmountGreaterThanOrderByIdAsc(BigDecimal.ZERO, PageRequest.of(0, limit));
    }
}